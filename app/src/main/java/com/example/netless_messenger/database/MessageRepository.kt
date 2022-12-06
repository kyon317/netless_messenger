package com.example.netless_messenger.database

import kotlinx.coroutines.flow.Flow

/* MessageRepository - Repository for view model to access and store database*/
class MessageRepository(private val messageDatabaseDao : MessageDatabaseDao) {
    val allComments: Flow<List<Message>> = messageDatabaseDao.getAllEntries()

    suspend fun insert(message : Message){
            messageDatabaseDao.insertEntry(message)
    }

    suspend fun delete(id: Long){
            messageDatabaseDao.deleteMessage(id)
    }

    suspend fun deleteUserMessage(userID: String){
            messageDatabaseDao.deleteUserMessage(userID)
    }

    suspend fun deleteAll(){
            messageDatabaseDao.deleteAll()
    }

    suspend fun getUserMessageEntries(userID: String): List<Message>{
            return messageDatabaseDao.getUserMessageEntries(userID)
    }

}