package com.example.netless_messenger.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageTestViewModel(application: Application): AndroidViewModel(application) {
    val allMessageLiveData: LiveData<List<Message>>
    private val repository: MessageRepository

    init {
        val messageDao = MessageDatabase.getInstance(application).MessageDatabaseDao
        repository = MessageRepository(messageDao)
        allMessageLiveData = repository.allComments.asLiveData()
    }

    fun insert(message : Message) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(message)
        }
    }

    fun deleteMessageById(id: Long){
        val messageList = allMessageLiveData.value
        if (messageList != null && messageList.size > 0){
            viewModelScope.launch(Dispatchers.IO) {
                repository.delete(id)
            }
        }
    }

    fun deleteAll(){
        val messageList = allMessageLiveData.value
        if (messageList != null && messageList.size > 0)
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteAll()
            }
    }

    fun deleteUserMessages(userId: String) {
        val messageList = allMessageLiveData.value
        if (messageList != null && messageList.size > 0)
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteUserMessage(userId)
            }
    }

    fun getById(index:Int):Message? {
        val messageList = allMessageLiveData.value

        if (messageList != null && messageList.isNotEmpty())
            return allMessageLiveData.value?.get(index)
        return null
    }

    fun getUserMessageEntries(userId: String):  List<Message>{
        val messageList = allMessageLiveData.value

        if (messageList != null && messageList.size > 0){
            return repository.getUserMessageEntries(userId)
        }
        return emptyList()
    }
}

