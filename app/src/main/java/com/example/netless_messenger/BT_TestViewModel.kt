package com.example.netless_messenger

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class BT_TestViewModel: ViewModel() {
    var availableDevices = MutableLiveData<ArrayList<BluetoothDevice>>()
    private var deviceArrayList: ArrayList<BluetoothDevice> = ArrayList()

    fun updateAvailableDevices(device:BluetoothDevice?){
        deviceArrayList.add(device!!)
        availableDevices.value = deviceArrayList
    }

    fun clearAvailableDevices(){
        availableDevices.value?.clear()
    }

    fun retrieveDeviceNames(): ArrayList<String>{
        val nameList = ArrayList<String>()
        for(device in availableDevices.value!!){
            nameList.add(device?.name)
        }
        return nameList
    }

    fun retrieveDevice(deviceName:String):BluetoothDevice?{
        for(device in availableDevices.value!!){
            if(deviceName == device.name){
                return device
            }
        }
        return null
    }
}