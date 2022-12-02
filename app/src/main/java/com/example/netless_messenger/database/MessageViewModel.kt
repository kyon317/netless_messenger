package com.example.netless_messenger.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData

/* UserViewModel - A view model that operates on ExerciseEntryRepository*/
class MessageViewModel(private val repository: MessageRepository) : ViewModel() {

    class CommentViewModel(private val repository: MessageRepository) : ViewModel() {
        val allCommentsLiveData: LiveData<List<Message>> = repository.allComments.asLiveData()

        fun insert(message : Message) {
            repository.insert(message)
        }

        fun deleteFirst(){
            val entryList = allCommentsLiveData.value
            if (entryList != null && entryList.size > 0){
                val id = entryList[0].id
                repository.delete(id)
            }
        }

        fun deleteAll(){
            val commentList = allCommentsLiveData.value
            if (commentList != null && commentList.size > 0)
                repository.deleteAll()
        }

        fun getById(index:Int):Message? {
            val commentList = allCommentsLiveData.value

            if (commentList != null && commentList.isNotEmpty())
                return allCommentsLiveData.value?.get(index)
            return null
        }

    }

}

class MessageViewModelFactory (private val repository: MessageRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if(modelClass.isAssignableFrom(MessageViewModel.CommentViewModel::class.java))
            return MessageViewModel.CommentViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/*  TODO:
*   HOW TO USE
        //
        private var entry : MessageEntry = Message()
        private lateinit var database: MessageDatabase
        private lateinit var databaseDao: MessageDatabaseDao
        private lateinit var repository: MessageRepository
        private lateinit var viewModelFactory: MessageViewModelFactory
        private lateinit var commentViewModel: MessageViewModel.CommentViewModel

        // set up database connection
        arrayList = ArrayList()
        arrayAdapter = MessageViewAdapter(this, arrayList)
        database = database.getInstance(this)
        databaseDao = database.UserDatabaseDao
        repository = MessageRepository(databaseDao)
        viewModelFactory = MessageViewModelFactory(repository)
        commentViewModel = ViewModelProvider(this, viewModelFactory).get(
            MessageViewModel.CommentViewModel::class.java)

        // update view model
        commentViewModel.allCommentsLiveData.observe(this, Observer { it ->
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        })*/