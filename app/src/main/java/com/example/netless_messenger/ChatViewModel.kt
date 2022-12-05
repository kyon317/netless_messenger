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
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel: ViewModel(), ServiceConnection {
    private var messageArrayList: ArrayList<com.example.netless_messenger.database.Message> = ArrayList()
    private var myMessageHandler: MessageHandler = MessageHandler(Looper.getMainLooper())
    var _currentMessageList = MutableLiveData<ArrayList<com.example.netless_messenger.database.Message>>()
    val currentMessageList: LiveData<ArrayList<com.example.netless_messenger.database.Message>>
        get() {
            return _currentMessageList
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
        }
    }

}