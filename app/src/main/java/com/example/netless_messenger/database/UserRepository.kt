package com.example.jiaqing_hu.database

import com.example.netless_messenger.database.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/* UserRepository - Repository for view model to access and store database*/
class UserRepository(private val userDatabaseDao : UserDatabaseDao) {
    //TODO: Refactor Val to allUsers
    val allComments: Flow<List<User>> = userDatabaseDao.getAllEntries()

    suspend fun insert(user: User){
            userDatabaseDao.insertEntry(user)
    }

    suspend fun delete(id: Long){
            userDatabaseDao.deleteComment(id)
    }

    suspend fun deleteAll(){
            userDatabaseDao.deleteAll()
    }

}