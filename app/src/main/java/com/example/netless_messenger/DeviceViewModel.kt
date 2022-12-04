package com.example.netless_messenger

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeviceViewModel:ViewModel(), ServiceConnection {
    private var deviceArrayList: ArrayList<BluetoothDevice> = ArrayList()
    private var myMessageHandler: MessageHandler = MessageHandler(Looper.getMainLooper())
    var _availableDevices = MutableLiveData<ArrayList<BluetoothDevice>>()
    val availableDevices: LiveData<ArrayList<BluetoothDevice>>
        get() {
            return _availableDevices
        }

    override fun onServiceConnected(name : ComponentName?, service : IBinder?) {
        val tempBinder = service as BluetoothServices.MyBinder
        tempBinder.setmsgHandler(myMessageHandler)
    }

    override fun onServiceDisconnected(name : ComponentName?) {

    }

    inner class MessageHandler(looper: Looper) : Handler(looper) {
        @SuppressLint("MissingPermission")
        override fun handleMessage(msg: Message) {
            // when msg is a device
            if (msg.what == BluetoothServices.DEVICE_VALUE) {
                val bundle = msg.data
                val currDevice = bundle.get("Device")!! as BluetoothDevice
                // update device list
                if(currDevice.name != null && !deviceArrayList.contains(currDevice)){
                        deviceArrayList.add(currDevice)
                        _availableDevices.value = deviceArrayList
                    }
            }
        }
    }

}