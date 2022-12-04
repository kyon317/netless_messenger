package com.example.netless_messenger.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/* MessageRepository - Repository for view model to access and store database*/
class MessageRepository(private val messageDatabaseDao : MessageDatabaseDao) {
    val allComments: Flow<List<Message>> = messageDatabaseDao.getAllEntries()

    suspend fun insert(message : Message){
            messageDatabaseDao.insertEntry(message)
    }

    suspend fun delete(id: Long){
            messageDatabaseDao.deleteComment(id)
    }

    suspend fun deleteAll(){
            messageDatabaseDao.deleteAll()
    }

}