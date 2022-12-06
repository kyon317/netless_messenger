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
        allUsersLiveData = repository.allComments.asLiveData()
    }

    fun insert(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(user)
        }
    }

    fun attempt_insert(user : User){
        viewModelScope.launch(Dispatchers.IO) {
            if (allUsersLiveData.value?.contains(user) != true)
                repository.insert(user)
        }
    }
    fun deleteFirst(){
        val entryList = allUsersLiveData.value
        if (entryList != null && entryList.size > 0){
            val id = entryList[0].id
            viewModelScope.launch(Dispatchers.IO) {
                repository.delete(id)
            }
        }
    }

    fun deleteAll(){
        val commentList = allUsersLiveData.value
        if (commentList != null && commentList.size > 0)
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteAll()
            }
    }

    fun getById(index:Int):User? {
        val commentList = allUsersLiveData.value

        if (commentList != null && commentList.isNotEmpty())
            return allUsersLiveData.value?.get(index)
        return null
    }


}