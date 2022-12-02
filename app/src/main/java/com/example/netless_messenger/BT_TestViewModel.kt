package com.example.netless_messenger

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class BT_TestViewModel: ViewModel() {
    var availableDevices = MutableLiveData<ArrayList<BluetoothDevice>>()
    var isConnected = MutableLiveData<Boolean>(false)

    private var deviceArrayList: ArrayList<BluetoothDevice> = ArrayList()

    fun updateAvailableDevices(device:BluetoothDevice?){
        if(device?.name != null && !deviceArrayList.contains(device)){
            deviceArrayList.add(device!!)
            availableDevices.value = deviceArrayList
        }
    }

    fun clearAvailableDevices(){
        availableDevices.value?.clear()
    }

    fun retrieveDeviceNames(context : Context): ArrayList<String>{
        val nameList = ArrayList<String>()
        for(device in availableDevices.value!!){
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
//                return
                Log.e(TAG, "retrieveDeviceNames: no permission", )
            }
            device?.name?.let { nameList.add(it) }
        }
        return nameList
    }

    //TODO: Write function to get is connected

    fun retrieveDevice(deviceName:String):BluetoothDevice?{
        for(device in availableDevices.value!!){
            if(deviceName == device.name){
                return device
            }
        }
        return null
    }
}