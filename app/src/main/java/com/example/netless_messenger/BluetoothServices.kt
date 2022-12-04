package com.example.netless_messenger

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.example.netless_messenger.database.Message
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothServices:Service() {
    private var connected = false
    private var btAdapter: BluetoothAdapter? = null
    private var btManager: BluetoothManager? = null
    val MY_UUID = UUID.fromString("578fa54b-a381-466f-970c-9200436d9981")

    private var connectThread: BluetoothServices.ConnectThread? = null
    private var acceptThread: BluetoothServices.AcceptThread? = null
    private var manageConnectionThread: BluetoothServices.ManageConnectionThread? = null
    private var message = Message()
    private var testMessage:String = ""
    private val TAG: String = "BT_TEST"

    private lateinit var deviceList : ArrayList<BluetoothClass.Device>

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        // Register for broadcasts when a device is discovered.
        val broadcastFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.applicationContext.registerReceiver(btReceiver, broadcastFilter)
        // Register for broadcasts when a msg is sent.
        val msgFilter = IntentFilter("SENDMSG")
        this.applicationContext.registerReceiver(msgReceiver, msgFilter)

        btManager = this.applicationContext?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        btAdapter = btManager?.adapter

        acceptThread = AcceptThread()
        acceptThread?.start()
        btAdapter?.startDiscovery()
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val btReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val btDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                TODO!! update device list
//                btViewModel.updateAvailableDevices(btDevice)
            }
        }
    }

    // Create a message body receiver
    private val msgReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val msgBody = intent.getStringExtra("msgBody")
            if (msgBody != null) {
//                setTestMessage(msgBody)
            }
        }
    }

    override fun onBind(intent : Intent?) : IBinder? {

    }

    //
    inner class MyBinder : Binder() {
        fun setmsgHandler(msgHandler: Handler) {
            this@Bluetooth.msgHandler = msgHandler
        }
    }

    // send received msg to database
    private fun sendMsg(){

    }


    // Accept Thread: Accept on connection, keep listening for a bluetooth connection
    @SuppressLint("MissingPermission")
    private inner class AcceptThread():Thread(){
        var btServerSocket: BluetoothServerSocket?

        init{
            var tmp: BluetoothServerSocket? = null
            try {
                tmp = btAdapter?.listenUsingInsecureRfcommWithServiceRecord("BT_TEST", MY_UUID)
            } catch (e: IOException) {
                Log.e(TAG, "Socket listen() failed", e)
            }

            btServerSocket = tmp
        }

        override fun run() {
            Log.d(TAG, "Socket Type: BEGIN accept Thread" + this)
            var socket: BluetoothSocket? = null

            try{
                socket = btServerSocket?.accept()
            } catch (e: IOException) {
                Log.e(TAG, "Socket accept() failed", e)
            }
//            activity.runOnUiThread(Runnable {
//                Toast.makeText(activity, "Socket accept() succeeded", Toast.LENGTH_SHORT).show()
//            })
            Log.e(TAG, "Socket accept() succeeded")

            manageConnectionThread = ManageConnectionThread(socket)
            manageConnectionThread?.start()
//            val testMessage = "This is a test"
//            manageConnectionThread?.writeOut(testMessage.toByteArray())
        }

        fun cancel(){
            Log.d(TAG, "Socket Type cancel " + this)
            try {
                btServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Socket close() of server failed", e)
            }
        }
    }

    // Manage Connection Thread: Manage connected thread, keep listening for new msg
    private inner class ManageConnectionThread(socket: BluetoothSocket?):Thread(){
        private val socket = socket
        private var iStream: InputStream?
        private var oStream: OutputStream?

        init {
            iStream = socket?.inputStream
            oStream = socket?.outputStream
        }

        override fun run() {
            val MAX_SIZE_SINGLE_MESSAGE = 1024
            var messageReceived = ByteArray(MAX_SIZE_SINGLE_MESSAGE)
            while(true)
            {
                try {
                    iStream?.read(messageReceived)
                }catch (e:IOException){
                    Log.e(TAG, "run: ", )
                }
                if(messageReceived[0].toInt() != 0)
                {
                    var charset = Charsets.UTF_8
                    val messageString = messageReceived.toString(charset)
                    // TODO: make a broadcast to notify DB

                }
            }
        }

        fun writeOut(message: ByteArray)
        {
            oStream?.write(message)
        }

        fun cancel() {
            try {
                socket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "close() of connected socket failed", e)
            }
        }
    }

    //Connect Thread: Start a connection to the accept threat of a specific device
    // If connection is established, return BEGIN connectThread otherwise, FAILURE
    @SuppressLint("MissingPermission")
    private inner class ConnectThread(btDevice: BluetoothDevice?):Thread(){
        var btSocket:BluetoothSocket?
        var device = btDevice

        init{
            var tmp: BluetoothSocket? = null
            btAdapter?.cancelDiscovery()
            try {
                tmp = device?.createInsecureRfcommSocketToServiceRecord(MY_UUID)
            } catch (e: IOException) {
                Log.e(TAG, "Socket connect create() failed", e)
            }

            btSocket = tmp
        }

        override fun run() {
            Log.i(TAG, "BEGIN connectThread")
            btAdapter?.cancelDiscovery()

            try {
                btSocket?.connect()
            } catch (e: IOException) {
                Log.e(TAG, "FAILURE connect Thread",e)
                // Close the socket
                try {
                    btSocket?.close()
                } catch (e2: IOException) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2)
                }

                cancel()
                return
            }

            manageConnectionThread = ManageConnectionThread(btSocket)
            manageConnectionThread?.start()

            synchronized(this) {
                connectThread = null
            }
        }

        fun cancel(){
            try {
                btSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "close() of connect socket failed", e)
            }
        }
    }
}