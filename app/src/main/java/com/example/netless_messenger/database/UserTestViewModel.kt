package com.example.netless_messenger.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.jiaqing_hu.database.UserDatabase
import com.example.jiaqing_hu.database.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserTestViewModel(application: Application): AndroidViewModel(application) {
    val allUsersLiveData: LiveData<List<User>>
    private val repository: UserRepository

    init {
        val userDao = UserDatabase.getInstance(application).userDatabaseDao
        repository = UserRepository(userDao)
        allUsersLiveData = repository.allUsers.asLiveData()
    }

    fun insert(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(user)
        }
    }


    fun attempt_insert(user : User){
        viewModelScope.launch(Dispatchers.IO) {
            var isDuplicate = false
            for (existingUser in allUsersLiveData.value!!){
                if (user.deviceMAC == existingUser.deviceMAC &&
                    user.deviceName == existingUser.deviceName){
                    isDuplicate = true
                }
            }
            if (!isDuplicate)
                repository.insert(user)
        }
    }

    fun deleteUserById(id: Long){
        val userList = allUsersLiveData.value
        if (userList != null && userList.size > 0){
            viewModelScope.launch(Dispatchers.IO) {
                repository.delete(id)
            }
        }
    }

    fun deleteAll(){
        val userList = allUsersLiveData.value
        if (userList != null && userList.size > 0)
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteAll()
            }
    }

    fun getById(index:Int):User? {
        val userList = allUsersLiveData.value

        if (userList != null && userList.isNotEmpty())
            return allUsersLiveData.value?.get(index)
        return null
    }

    fun updateUserNameAndAvatarById(id: Long, name: String, avatar: Int) {
        val userList = allUsersLiveData.value

        if (userList != null && userList.size > 0){
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateUserInfo(id, name, avatar)
            }
        }
    }

}