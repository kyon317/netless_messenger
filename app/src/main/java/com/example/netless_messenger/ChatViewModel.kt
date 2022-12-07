package com.example.netless_messenger

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Intent
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
    //TODO: Chat activity should observe this flag; when set to false, service is not running.
    // IT IS THE RESPONSIBILITY OF THE OBSERVER TO RESTART THE SERVICE IMMEDIATELY AND CALL
    // resetFlag_isConnectionServiceRunning(). When the flag is reset, observer should ignore the
    // flag when it is true.
    // If the chat activity is not running when the service is terminated, it is the responsibility
    // of the activity to query this flag at the earliest possible opportunity and restart the
    // service if necessary.
    private var _isConnectionServiceRunning = MutableLiveData<Boolean>()
    val isConnectionServiceRunning:LiveData<Boolean>
        get() {
            return _isConnectionServiceRunning
        }

    private var _isConnectedToDevice = MutableLiveData<Boolean>()
    val isConnectedToDevice:LiveData<Boolean>
        get() {
            return _isConnectedToDevice
        }
    var deviceName = MutableLiveData<String>()
    var deviceAddress = MutableLiveData<String>()
    private var messageArrayList: ArrayList<com.example.netless_messenger.database.Message> = ArrayList()
    private var myMessageHandler: MessageHandler = MessageHandler(Looper.getMainLooper())
    var _currentMessageList = MutableLiveData<ArrayList<com.example.netless_messenger.database.Message>>()
    val currentMessageList: LiveData<ArrayList<com.example.netless_messenger.database.Message>>
        get() {
            return _currentMessageList
        }

    init {
       _isConnectionServiceRunning.value = true
        _isConnectedToDevice.value = false
    }

    //Live data observer ignores when this flag is true
    fun resetFlag_isConnectionServiceRunning(){
        _isConnectionServiceRunning.value = true
    }

    fun getFlag_isConnectionServiceRunning() : Boolean{
        return _isConnectionServiceRunning.value!!
    }

    fun getFlag_isConnectedToDevice() : Boolean{
        return _isConnectedToDevice.value!!
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
                _isConnectionServiceRunning.value = false
//                _isConnectedToDevice.value = false
                _isConnectedToDevice.value = msg.data.getBoolean("ConnectionStatus")
            }
            if (msg.what == ConnectionService.CONNECTION_SUCCEEDED){
                deviceName.value = msg.data.getString("Name")
                deviceAddress.value = msg.data.getString("Address")
                _isConnectedToDevice.value = msg.data.getBoolean("ConnectionStatus")
//                _isConnectedToDevice.value = true
            }
        }
    }

}