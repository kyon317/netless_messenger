package com.example.netless_messenger.database

import com.example.netless_messenger.database.User
import com.example.netless_messenger.database.UserDatabaseDao
import kotlinx.coroutines.flow.Flow

/* UserRepository - Repository for view model to access and store database*/
class UserRepository(private val userDatabaseDao : UserDatabaseDao) {
    //TODO: Refactor Val to allUsers
    val allUsers: Flow<List<User>> = userDatabaseDao.getAllEntries()

    suspend fun insert(user: User){
            userDatabaseDao.insertEntry(user)
    }

    suspend fun delete(id: Long){
            userDatabaseDao.deleteUser(id)
    }

    suspend fun deleteAll(){
            userDatabaseDao.deleteAll()
    }

    suspend fun updateUserInfo(id: Long, name: String, avatar: Int){
            userDatabaseDao.updateUser(id, name, avatar)
    }

}