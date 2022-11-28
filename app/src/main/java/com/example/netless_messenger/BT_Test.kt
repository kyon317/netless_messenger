package com.example.netless_messenger

import android.Manifest
import android.app.Activity
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class BT_Test(activity: Activity, context: Context, btViewModel: BT_TestViewModel) {

    private val activity = activity
    private val context = context
    private var connected = false
    private var btAdapter: BluetoothAdapter?
    private var btManager: BluetoothManager?
    val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    //val uuid = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB")

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val btReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (BluetoothDevice.ACTION_FOUND == action) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val btDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = btDevice?.name
                val deviceHardwareAddress = btDevice?.address // MAC address

                //for testing with Armaan's second device
                /*if(deviceName == "OnePlus 6T"){
                    connectDevice(btDevice)
                }*/

                /*if(deviceName == "Armaan's Galaxy S9"){
                    connectDevice(btDevice)
                }*/

                //TODO: Display list of available connections
                btViewModel.updateAvailableDevices(btDevice) //does this need to be suspend?
            }
        }
    }

    init {
        // Register for broadcasts when a device is discovered.
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(btReceiver, filter)

        btManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
         btAdapter = btManager?.adapter
        val listenThread = Thread(){
            startListening()
        }
        listenThread.start()
        val discoverThread = Thread(){
            btAdapter?.startDiscovery()
        }
        discoverThread.start()
    }

    //TODO not working
    private fun startListening()
    {
        var btServerSocket: BluetoothServerSocket?
        btServerSocket = btAdapter?.listenUsingInsecureRfcommWithServiceRecord("BT_Test", uuid)
        //btServerSocket = btAdapter?.listenUsingRfcommWithServiceRecord("BT_Test", uuid)
        var btSocket: BluetoothServerSocket? = null
        while(!connected){
            try {
                btServerSocket?.accept()
            } catch (e: IOException) {
                Log.e("Socket Accept Failed", "Socket's accept() method failed", e)
                null
            }
            btSocket = btServerSocket
            connected = true
        }
        if (btSocket != null){
            Log.d("Here","Here")
        }

    }

    //Called by view model when device is selected from device view
    fun connectDevice(btDevice: BluetoothDevice?)
    {
        btAdapter?.cancelDiscovery()

        var btSocket: BluetoothSocket?

        btSocket = btDevice?.createInsecureRfcommSocketToServiceRecord(uuid)
        //btSocket = btDevice?.createRfcommSocketToServiceRecord(uuid)
        //btSocket = btDevice?.createInsecureRfcommSocketToServiceRecord(UUID.randomUUID())
        //btSocket = btDevice?.createRfcommSocketToServiceRecord(UUID.randomUUID())

        //val pairedDevices = btAdapter?.bondedDevices //connection will fail if already bonded
        Thread.sleep(1000)
        btSocket?.connect()
        Log.d("Here","Here")

    }
}