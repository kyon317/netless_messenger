package com.example.netless_messenger.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/* MessageRepository - Repository for view model to access and store database*/
class MessageRepository(private val messageDatabaseDao : MessageDatabaseDao) {
    val allComments: Flow<List<Message>> = messageDatabaseDao.getAllEntries()

    fun insert(message : Message){
        CoroutineScope(Dispatchers.IO).launch{
            messageDatabaseDao.insertEntry(message)
        }
    }

    fun delete(id: Long){
        CoroutineScope(Dispatchers.IO).launch {
            messageDatabaseDao.deleteComment(id)
        }
    }

    fun deleteAll(){
        CoroutineScope(Dispatchers.IO).launch {
            messageDatabaseDao.deleteAll()
        }
    }

}