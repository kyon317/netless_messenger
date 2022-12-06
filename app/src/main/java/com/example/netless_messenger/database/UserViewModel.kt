package com.example.jiaqing_hu.database

import androidx.lifecycle.*
import com.example.netless_messenger.database.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/* UserViewModel - A view model that operates on ExerciseEntryRepository*/
class UserViewModel(private val repository: UserRepository) : ViewModel() {

    class CommentViewModel(private val repository: UserRepository) : ViewModel() {
        val allCommentsLiveData: LiveData<List<User>> = repository.allUsers.asLiveData()

        fun insert(user: User) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.insert(user)
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

        fun getById(index:Int):User? {
            val commentList = allCommentsLiveData.value

            if (commentList != null && commentList.isNotEmpty())
                return allCommentsLiveData.value?.get(index)
            return null
        }

    }

}

class ExerciseViewModelFactory (private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if(modelClass.isAssignableFrom(UserViewModel.CommentViewModel::class.java))
            return UserViewModel.CommentViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/*  TODO:
*   HOW TO USE
        //
        private var entry : UserEntry = User()
        private lateinit var database: UserDatabase
        private lateinit var databaseDao: UserDatabaseDao
        private lateinit var repository: UserRepository
        private lateinit var viewModelFactory: UserViewModelFactory
        private lateinit var commentViewModel: UserViewModel.CommentViewModel

        // set up database connection
        arrayList = ArrayList()
        arrayAdapter = UserViewAdapter(this, arrayList)
        database = database.getInstance(this)
        databaseDao = database.UserDatabaseDao
        repository = UserRepository(databaseDao)
        viewModelFactory = UserViewModelFactory(repository)
        commentViewModel = ViewModelProvider(this, viewModelFactory).get(
            UserViewModel.CommentViewModel::class.java)

        // update view model
        commentViewModel.allCommentsLiveData.observe(this, Observer { it ->
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        })*/