package com.example.netless_messenger

import android.app.Activity
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.ParcelUuid
import android.provider.Settings
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Byte
import java.util.*


class BT_Test(activity: Activity, context: Context, btViewModel: BT_TestViewModel) {

    private var connected = false
    private val context = context
    private val activity = activity
    private var btAdapter: BluetoothAdapter?
    private var btManager: BluetoothManager?
    //val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        val MY_UUID = UUID.fromString("00001103-0000-1000-8000-00805f9b34fb") //Taryn's works?
        //val MY_UUID = UUID.fromString("0000110a-0000-1000-8000-00805f9b34fb") //Taryn's alt 1
        //val MY_UUID = UUID.fromString("00001105-0000-1000-8000-00805f9b34fb") //Taryn's alt 2
        //val MY_UUID = UUID.fromString("00001106-0000-1000-8000-00805f9b34fb") //Taryn's alt 3 works?
        //val MY_UUID = UUID.fromString("00001115-0000-1000-8000-00805f9b34fb") //Taryn's alt 4
        //val MY_UUID = UUID.fromString("00001116-0000-1000-8000-00805f9b34fb") //Taryn's alt 5
        //val MY_UUID = UUID.fromString("0000110e-0000-1000-8000-00805f9b34fb") //Taryn's alt 6
        //val MY_UUID = UUID.fromString("0000112f-0000-1000-8000-00805f9b34fb") //Taryn's alt 7 works?
        //val MY_UUID = UUID.fromString("00001112-0000-1000-8000-00805f9b34fb") //Taryn's alt 8 works?
        //val MY_UUID = UUID.fromString("0000111f-0000-1000-8000-00805f9b34fb") //Taryn's alt 9 works?
        //val MY_UUID = UUID.fromString("00001132-0000-1000-8000-00805f9b34fb") //Taryn's alt 9

        //val MY_UUID = UUID.fromString("0000110a-0000-1000-8000-00805f9b34fb") //Armaan and? Brayden's
        //val MY_UUID = UUID.fromString("00001105-0000-1000-8000-00805f9b34fb") //Armaan alt 1
        //val MY_UUID = UUID.fromString("00001115-0000-1000-8000-00805f9b34fb") //Armaan alt 2
        //val MY_UUID = UUID.fromString("0000110e-0000-1000-8000-00805f9b34fb") //Armaan alt 3
        //val MY_UUID = UUID.fromString("0000112f-0000-1000-8000-00805f9b34fb") //Armaan alt 4 This works!!!!!!!!!!! but same thing doesn't return accept
        //val MY_UUID = UUID.fromString("00001112-0000-1000-8000-00805f9b34fb") //Armaan alt 5
        //val MY_UUID = UUID.fromString("0000111f-0000-1000-8000-00805f9b34fb") //Armaan alt 6
        //val MY_UUID = UUID.fromString("00001132-0000-1000-8000-00805f9b34fb") //Armaan alt 7
        //val MY_UUID = UUID.fromString("594a34fc-31db-11ea-978f-2e728ce88125") //Armaan alt 8
    //c407b4ee917e309c

    //val MY_UUID = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB")
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
                val uuidParcel = device?.uuids
                val uuid = uuidParcel?.get(0)?.uuid
                tmp = device?.createInsecureRfcommSocketToServiceRecord(MY_UUID)
            } catch (e: IOException) {
                Log.e(TAG, "Socket connect create() failed", e)
            }

            btSocket = tmp
        }

        override fun run() {
            Log.i(TAG, "BEGIN connectThread")
            btAdapter?.cancelDiscovery()

            //fetchUUIDs(device)

            // To remove bonded device
//            if(btAdapter?.bondedDevices?.size!! > 0)
//            {
//                try {
//                    device!!::class.java.getMethod("removeBond").invoke(device)
//                } catch (e: Exception) {
//                    Log.e(TAG, "Removing bond has been failed. ${e.message}")
//                }
//            }
//            val temp = btAdapter?.bondedDevices

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

        //Retrieve UUID List from device
//        fun fetchUUIDs(device:BluetoothDevice?)
//        {
//            var uuidParcel = device?.uuids
//            val name = device?.name
//            var uuid = uuidParcel?.get(0)?.uuid
//            while(uuid == null)
//            {
//                uuidParcel = device?.uuids
//                uuid = uuidParcel?.get(0)?.uuid
//            }
//
//            return
//        }
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
            while(true)
            {
                readIn(messageReceived)
                if(messageReceived[0].toInt() != 0)
                {
                    var charset = Charsets.UTF_8
                    val messageAsString = messageReceived.toString(charset)
                    activity.runOnUiThread(Runnable {
                        Toast.makeText(activity,"$messageAsString", Toast.LENGTH_LONG).show()
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
