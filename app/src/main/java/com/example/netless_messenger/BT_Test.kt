package com.example.netless_messenger

import android.app.Activity
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class BT_Test(activity: Activity, context: Context, btViewModel: BT_TestViewModel) {

    private var connected = false
    private val context = context
    private val activity = activity
    private var btAdapter: BluetoothAdapter?
    private var btManager: BluetoothManager?
        val MY_UUID = UUID.fromString("578fa54b-a381-466f-970c-9200436d9981")

    private var connectThread: ConnectThread? = null
    private var acceptThread: AcceptThread? = null
    private var manageConnectionThread: ManageConnectionThread? = null

    private val  TAG: String = "BT_TEST"

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val btReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (BluetoothDevice.ACTION_FOUND == action) {
                val btDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                btViewModel.updateAvailableDevices(btDevice)
            }
        }
    }

    init {
        // Register for broadcasts when a device is discovered.
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(btReceiver, filter)

        btManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        btAdapter = btManager?.adapter

        acceptThread = AcceptThread()
        acceptThread?.start()
        btAdapter?.startDiscovery()
    }

    fun attemptConnection(btDevice: BluetoothDevice?)
    {
        connectThread = ConnectThread(btDevice)
        connectThread?.start()
    }

    private inner class AcceptThread():Thread(){
        var btServerSocket:BluetoothServerSocket?

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
            Log.d(TAG, "Socket Type: BEGIN acceptThread" + this)
            var socket:BluetoothSocket? = null

            try{
                socket = btServerSocket?.accept()
            } catch (e: IOException) {
                Log.e(TAG, "Socket accept() failed", e)
            }
            activity.runOnUiThread(Runnable {
                Toast.makeText(activity, "Socket accept() succeeded", Toast.LENGTH_SHORT).show()
            })
            Log.e(TAG, "Socket accept() succeeded")

            manageConnectionThread = ManageConnectionThread(socket)
            manageConnectionThread?.start()
            val testMessage = "Testing! Testing!"
            manageConnectionThread?.writeOut(testMessage.toByteArray())
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
            activity.runOnUiThread(Runnable {
                Toast.makeText(activity, "Socket connect() succeeded", Toast.LENGTH_SHORT).show()
            })

            manageConnectionThread = ManageConnectionThread(btSocket)
            manageConnectionThread?.start()

            synchronized(this@BT_Test) {
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
            var tempBool = true
            while(tempBool)
            {
                readIn(messageReceived)
                if(messageReceived[0].toInt() != 0)
                {
                    var charset = Charsets.UTF_8
                    val messageAsString = messageReceived.toString(charset)
                    activity.runOnUiThread(Runnable {
                        Toast.makeText(activity,"$messageAsString", Toast.LENGTH_LONG).show()
                        tempBool = false
                    })
                }

            }
        }

        fun readIn(messageReceived: ByteArray){
            try {
                iStream?.read(messageReceived)
            } catch (e: IOException) {

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
