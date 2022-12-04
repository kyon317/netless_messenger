package com.example.netless_messenger

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
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

class ConnectionService() : Service() {
    private var msgHandler: Handler? = null
    private var btManager: BluetoothManager? = null
    private var btAdapter: BluetoothAdapter? = null
    private lateinit var myBinder:MyBinder
    private var connectThread: ConnectionService.ConnectThread? = null
    private var manageConnectionThread: ConnectionService.ManageConnectionThread? = null
    private var MY_UUID = UUID.fromString("578fa54b-a381-466f-970c-9200436d9981")
    private lateinit var btDevice : BluetoothDevice

    override fun onCreate() {
        super.onCreate()
        btManager = this.applicationContext?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        btAdapter = btManager?.adapter
        myBinder = MyBinder()
        // Register for broadcasts when a msg is sent.
        val msgFilter = IntentFilter("SENDMSG")
        this.applicationContext.registerReceiver(msgReceiver, msgFilter)
    }


    override fun onStartCommand(intent : Intent?, flags : Int, startId : Int) : Int {
        if (intent != null) {
            btDevice = intent.getParcelableExtra<BluetoothDevice>("Device")!!
            attemptConnection()
        }
        return START_STICKY
    }

    // TODO: Create a message body receiver
    private val msgReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val msgBody = intent.getStringExtra("msgBody")
            if (msgBody != null) {
                sendMessage(msgBody)
            }
        }
    }

    // try to send Message
    private fun sendMessage(msgBody:String){
        manageConnectionThread?.writeOut(msgBody.toByteArray())
    }

    // attempt connection to the selected device
    private fun attemptConnection()
    {
        connectThread = ConnectThread(btDevice)
        connectThread?.start()
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


    //Connect Thread: Start a connection to the accept threat of a specific device
    // If connection is established, return BEGIN connectThread otherwise, FAILURE
    @SuppressLint("MissingPermission")
    private inner class ConnectThread(btDevice: BluetoothDevice?):Thread(){
        var btSocket: BluetoothSocket?
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
//            val testMessage = "This is a test"
//            manageConnectionThread?.writeOut(testMessage.toByteArray())
//            synchronized(this) {
//                connectThread = null
//            }
        }

        fun cancel(){
            try {
                btSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "close() of connect socket failed", e)
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
                    Log.e(TAG, "run: ${messageReceived.toString(Charsets.UTF_8)}", )
                    val test = "this is a test string"
                    // TODO: Suck when trying to read messageReceived, uncomment below and add break pt to see
                    iStream?.read(messageReceived,0,MAX_SIZE_SINGLE_MESSAGE)
//                    iStream?.read(test.toByteArray())
                }catch (e:IOException){
                    Log.e(TAG, "run: ",e )
                }
                if(messageReceived[0].toInt() != 0)
                {
                    var charset = Charsets.UTF_8
                    val messageString = messageReceived.toString(charset)
                    // TODO: make a broadcast to notify DB
                    val message = Message()
                    message.msgBody = iStream.toString()
                    message.status = "rcv"
                    MainActivity.messageTest.insert(message)
                    Log.e(TAG, "run: received $messageString" )
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

}