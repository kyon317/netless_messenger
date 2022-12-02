package com.example.jiaqing_hu.database

import com.example.netless_messenger.database.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/* UserRepository - Repository for view model to access and store database*/
class UserRepository(private val userDatabaseDao : UserDatabaseDao) {
    val allComments: Flow<List<User>> = userDatabaseDao.getAllEntries()

    fun insert(user: User){
        CoroutineScope(Dispatchers.IO).launch{
            userDatabaseDao.insertEntry(user)
        }
    }

    fun delete(id: Long){
        CoroutineScope(Dispatchers.IO).launch {
            userDatabaseDao.deleteComment(id)
        }
    }

    fun deleteAll(){
        CoroutineScope(Dispatchers.IO).launch {
            userDatabaseDao.deleteAll()
        }
    }

}