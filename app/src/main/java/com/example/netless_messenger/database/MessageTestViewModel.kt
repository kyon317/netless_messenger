package com.example.netless_messenger.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageTestViewModel(application: Application): AndroidViewModel(application) {
    val allCommentsLiveData: LiveData<List<Message>>
    private val repository: MessageRepository

    init {
        val messageDao = MessageDatabase.getInstance(application).MessageDatabaseDao
        repository = MessageRepository(messageDao)
        allCommentsLiveData = repository.allComments.asLiveData()
    }

    fun insert(message : Message) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(message)
        }
    }

    fun deleteFirst(){
        val entryList = allCommentsLiveData.value
        if (entryList != null && entryList.size > 0){
            val id = entryList[0].id
            viewModelScope.launch(Dispatchers.IO) {
                repository.delete(id)
            }
        }
    }

    fun deleteAll(){
        val commentList = allCommentsLiveData.value
        if (commentList != null && commentList.size > 0)
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteAll()
            }
    }

    fun getById(index:Int):Message? {
        val commentList = allCommentsLiveData.value

        if (commentList != null && commentList.isNotEmpty())
            return allCommentsLiveData.value?.get(index)
        return null
    }


}

