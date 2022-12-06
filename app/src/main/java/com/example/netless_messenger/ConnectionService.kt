package com.example.netless_messenger

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.example.netless_messenger.database.Message
import com.example.netless_messenger.database.User
import com.example.netless_messenger.ui.main.MainFragment
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

//TODO: Handle connection terminated
//TODO: Restart bluetooth and connection service where needed

class ConnectionService() : Service() {
    companion object  {
        const val MESSAGE_VALUE = 0
        const val RESET_CONNECTION_SERVICE = 1
        const val CONNECTION_SUCCEEDED = 2
    }

    private var msgHandler: Handler? = null
    private var btManager: BluetoothManager? = null
    private var btAdapter: BluetoothAdapter? = null
    private lateinit var myBinder:MyBinder
    private var MY_UUID = UUID.fromString("578fa54b-a381-466f-970c-9200436d9981")
    private var btDevice : BluetoothDevice? = null
    private var bluetoothSocket : BluetoothSocket?=null
    var isBlueConnected: Boolean = false
    private val ENCODING_FORMAT = "GBK"
    private val TAG = "ConnectionService"

    // User DB track list
    private var currentContactList : ArrayList<String> = ArrayList()
    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        btManager = this.applicationContext?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        btAdapter = btManager?.adapter
        myBinder = MyBinder()

        // Register for broadcasts when a msg is sent.
        val msgFilter = IntentFilter("SENDMSG")
        val attemptConnectionFilter = IntentFilter("ATTEMPT_CONNECTION")
        val killCurrentServiceFilter = IntentFilter("KILL_CONNECTION")
        this.applicationContext.registerReceiver(broadcastReceiver, msgFilter)
        this.applicationContext.registerReceiver(broadcastReceiver, attemptConnectionFilter)
        this.applicationContext.registerReceiver(broadcastReceiver, killCurrentServiceFilter)

        funStartListeningForConnection()
    }


    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent : Intent?, flags : Int, startId : Int) : Int {
        return START_STICKY
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(intent.action == "SENDMSG")
            {
                val msgBody = intent.getStringExtra("msgBody")
                Log.e(TAG,"Testing $msgBody")
                if (msgBody != null) {
                    funBlueClientSend(msgBody)
                }
            }
            else if(intent.action == "ATTEMPT_CONNECTION")
            {
                btDevice = intent.getParcelableExtra<BluetoothDevice>("SELECTED_DEVICE")!!
                funStartBlueClientConnect()
            }
            else if(intent.action == "KILL_CONNECTION")
            {
                disconnect()
            }
        }
    }

    // start Blue tooth client
    @SuppressLint("MissingPermission")
    private fun funStartBlueClientConnect() {
        Log.e(TAG,"Testing2")
        thread {
            bluetoothSocket = btDevice?.createInsecureRfcommSocketToServiceRecord(MY_UUID)
            try {
                // will block if runs in main thread
                if (bluetoothSocket != null || !isBlueConnected) {
                    btAdapter?.cancelDiscovery()
                    bluetoothSocket!!.connect()
                    Log.e(TAG, "Connected")
                    isBlueConnected = true

                    sendConnectionSucceededMessage()

                    funBlueClientStartReceive()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                disconnect()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun funStartListeningForConnection(){
        thread {
            var btServerSocket: BluetoothServerSocket?
            var tmp: BluetoothServerSocket? = null
            try {
                tmp = btAdapter?.listenUsingInsecureRfcommWithServiceRecord(TAG, MY_UUID)
            } catch (e: IOException) {
                Log.e(TAG, "Socket listen() failed", e)
                disconnect()
            }
            btServerSocket = tmp

            Log.e(TAG, "Socket Type: Start accept Thread" + this)

            try{
                bluetoothSocket = btServerSocket?.accept()
            } catch (e: IOException) {
                Log.e(TAG, "Socket accept() failed", e)
                disconnect()
            }
//            activity.runOnUiThread(Runnable {
//                Toast.makeText(activity, "Socket accept() succeeded", Toast.LENGTH_SHORT).show()
//            })
            Log.e(TAG, "Socket accept() succeeded")


            isBlueConnected = true

            btDevice = bluetoothSocket?.remoteDevice

            val deviceDialogSuccessIntent = Intent()
            deviceDialogSuccessIntent.action = "CONNECTION_SUCCESSFUL"
            sendBroadcast(deviceDialogSuccessIntent)

            sendConnectionSucceededMessage()
            funBlueClientStartReceive()

            //Close server socket because we have bluetooth socket and don't need it anymore
            try {
                btServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Socket close() of server failed", e)
            }
        }
    }

    // start receiving
    private fun funBlueClientStartReceive() {
        thread {
            while (true) {
                try {
                    if (bluetoothSocket != null) {
                        if (bluetoothSocket!!.isConnected) {
                            Log.e(TAG, "Connection available")
                            receiveMessage()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "funBlueClientStartReceive:" + e.toString())
                    disconnect()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun funInstantiateNewContact(currSocket : BluetoothSocket) {
        val contact:User = User()
        contact.deviceName = currSocket.remoteDevice.name
        contact.deviceMAC = currSocket.remoteDevice.address
//        contact.deviceID = currSocket.remoteDevice.uuids.toString()
        contact.userName = currSocket.remoteDevice.name
        val mainFragmentViewModel = MainFragment.userViewModel

        if (mainFragmentViewModel.allUsersLiveData.value != null){
            for (users in mainFragmentViewModel.allUsersLiveData.value!!){
                currentContactList.add(users.deviceMAC)
            }
            if(!currentContactList.contains(contact.deviceMAC)){
                val senderIntent = Intent()
                senderIntent.action = "INSERTION_REQUIRED"
                senderIntent.putExtra("CONTACT",contact)
                sendBroadcast(senderIntent)
//                mainFragmentViewModel.attempt_insert(contact)
            }
        }
    }

    // receive and parse message
    private fun receiveMessage() {
        val mmInStream: InputStream = bluetoothSocket!!.inputStream
        val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream
        var bytes = 0
        while (true) {
            // read from the InputStream.
            try {
                bytes = mmInStream.read(mmBuffer)
            } catch (e: IOException) {
                Log.d(TAG, "Input stream was disconnected", e)
                disconnect()
            }
            if(mmBuffer[0].toInt() != 0)
            {
                Log.d(TAG, "$mmBuffer")
            }

            val message = android.os.Message()
            val bundle = Bundle()
            // GBK encoding by default
            val string = String(mmBuffer, 0, bytes, Charset.forName(ENCODING_FORMAT))
            bundle.putString("Message", string)
            message.what = MESSAGE_VALUE   // 0 = rcv
            message.data = bundle
            this@ConnectionService.msgHandler?.sendMessage(message)
            if (string.isNotEmpty()){
                val rcv_msg = Message()
                rcv_msg.msgBody = string
                rcv_msg.status = "rcv"
                rcv_msg.userID = btDevice?.address ?: String()
                MainActivity.messageTest.insert(rcv_msg)
            }

            Log.e("receive", string)
        }
    }

    // send message via bluetooth
    private fun funBlueClientSend(input: String) {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.outputStream.write(input.toByteArray(Charset.forName(ENCODING_FORMAT)))
                Log.e(TAG, "funBlueClientSend: ${input}", )
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "sendCommand: Failed to send message", e)
                disconnect()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendConnectionSucceededMessage()
    {
        val message = android.os.Message()
        val bundle = Bundle()
        val name = btDevice?.name
        val address = btDevice?.address
        bundle.putString("Name", name)
        bundle.putString("Address", address)
        message.what = CONNECTION_SUCCEEDED
        message.data = bundle
        funInstantiateNewContact(bluetoothSocket!!)
        this@ConnectionService.msgHandler?.sendMessage(message)
    }

    // disconnect current bluetooth connection
    private fun disconnect() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket!!.close()
                bluetoothSocket = null
                isBlueConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "disconnect: failed to disconnect", e)
            }
        }

        val message = android.os.Message()
        message.what = RESET_CONNECTION_SERVICE
        this@ConnectionService.msgHandler?.sendMessage(message)
        Log.e(TAG, "Reset flag set")

        this.stopSelf()
    }


    // Bind & Unbind
    override fun onBind(intent : Intent?) : IBinder? {
        Log.e(TAG, "onBind: debug: Service onBind() called")
        return myBinder
    }
    override fun onUnbind(intent: Intent?): Boolean {
        Log.e(TAG, "onUnbind: debug: Service onUnBind() called~~~", )
        msgHandler = null
        return true
    }

    inner class MyBinder : Binder() {
        fun setmsgHandler(msgHandler: Handler) {
            this@ConnectionService.msgHandler = msgHandler
        }
    }

}