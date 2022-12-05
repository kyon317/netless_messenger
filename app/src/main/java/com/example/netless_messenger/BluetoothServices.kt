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
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.concurrent.thread

class BluetoothServices:Service() {
    private var btAdapter: BluetoothAdapter? = null
    private var btManager: BluetoothManager? = null

    private var socket: BluetoothSocket? = null
    private var message = Message()
    private var msgHandler: Handler? = null
    private val TAG: String = "BT_Services"

    private lateinit var deviceList : ArrayList<BluetoothClass.Device>
    private lateinit var myBinder: MyBinder

    companion object{
        const val DEVICE_VALUE = 0
        const val MESSAGE_VALUE = 1
    }


    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        // Register for broadcasts when a device is discovered.
        val broadcastFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.applicationContext.registerReceiver(btReceiver, broadcastFilter)

        btManager = this.applicationContext?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        btAdapter = btManager?.adapter

        btAdapter?.startDiscovery()

        myBinder = MyBinder()
        Log.e(TAG, "onCreate: debug: Service onCreate() called")
    }

    override fun onStartCommand(intent : Intent?, flags : Int, startId : Int) : Int {
        return START_STICKY
    }
    // Create a BroadcastReceiver for ACTION_FOUND.
    private val btReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val btDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (btDevice != null) {
//                    Log.e(TAG, "onReceive: ${btDevice.name}")
                    val deviceMsg = msgHandler?.obtainMessage()
                    if (deviceMsg != null) {
                        val bundle = Bundle()
                        bundle.putParcelable("Device",btDevice)
                        deviceMsg.data = bundle
                        deviceMsg.what = DEVICE_VALUE
                        msgHandler?.sendMessage(deviceMsg)
                    }else
                    {
                        Log.e(TAG, "onReceive: dmsg is null" )
                    }
                }
            }
        }
    }

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
            this@BluetoothServices.msgHandler = msgHandler
        }
    }

}