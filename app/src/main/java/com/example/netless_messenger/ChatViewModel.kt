package com.example.netless_messenger

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel: ViewModel(), ServiceConnection {
    private var isConnectionServiceRunning = MutableLiveData<Boolean>()
    private var isConnectedToDevice = MutableLiveData<Boolean>()
    private var deviceName = MutableLiveData<String>()
    private var deviceAddress = MutableLiveData<String>()
    private var messageArrayList: ArrayList<com.example.netless_messenger.database.Message> = ArrayList()
    private var myMessageHandler: MessageHandler = MessageHandler(Looper.getMainLooper())
    var _currentMessageList = MutableLiveData<ArrayList<com.example.netless_messenger.database.Message>>()
    val currentMessageList: LiveData<ArrayList<com.example.netless_messenger.database.Message>>
        get() {
            return _currentMessageList
        }

    init {
       isConnectionServiceRunning.value = true
        isConnectedToDevice.value = false
    }

    //Live data observer ignores when this flag is true
    fun resetFlag_isConnectionServiceRunning(){
        isConnectionServiceRunning.value = true
    }

    fun resetFlag_isConnectedToDevice(){
        isConnectedToDevice.value = false
    }

    fun getFlag_isConnectionServiceRunning() : Boolean{
        return isConnectionServiceRunning.value!!
    }

    fun getFlag_isConnectedToDevice() : Boolean{
        return isConnectedToDevice.value!!
    }

    override fun onServiceConnected(name : ComponentName?, service : IBinder?) {
        val tempBinder = service as ConnectionService.MyBinder
        tempBinder.setmsgHandler(myMessageHandler)
    }

    override fun onServiceDisconnected(name : ComponentName?) {

    }

    inner class MessageHandler(looper: Looper) : Handler(looper) {
        @SuppressLint("MissingPermission")
        override fun handleMessage(msg: Message) {
            // when msg is a device
            if (msg.what == ConnectionService.MESSAGE_VALUE) {
                val bundle = msg.data
                val currentMessage = bundle.get("Message") as String
                // update device list
                if(currentMessage.isNotEmpty()){
                    val tempMsg = com.example.netless_messenger.database.Message()
                    tempMsg.msgBody = currentMessage
                    tempMsg.status = "rcv"
                    messageArrayList.add(tempMsg)
                    _currentMessageList.value = messageArrayList
                    Log.e(TAG, "handleMessage: current msg received is $currentMessage" )
                }
            }
            if (msg.what == ConnectionService.RESET_CONNECTION_SERVICE){
                isConnectionServiceRunning.value = false
            }
            if (msg.what == ConnectionService.CONNECTION_SUCCEEDED){
                val bundle = msg.data
                deviceName.value = bundle.getString("Name")
                deviceAddress.value = bundle.getString("Address")
                isConnectedToDevice.value = true
            }
        }
    }

}