package com.example.netless_messenger

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.database.*
import com.example.netless_messenger.ui.main.MessageViewAdapter

class ChatActivity: AppCompatActivity() {
    private var entry : Message = Message()
    private lateinit var database: MessageDatabase
    private lateinit var databaseDao: MessageDatabaseDao
    private lateinit var repository: MessageRepository
    private lateinit var viewModelFactory: MessageViewModelFactory
    private lateinit var commentViewModel: MessageViewModel.CommentViewModel

    private lateinit var editText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity_layout)
        editText = findViewById(R.id.chat_input_edit_text)
        sendButton = findViewById(R.id.send_button)
        messageRecyclerView = findViewById(R.id.message_list)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)


        val uName = intent.getStringExtra("userName").toString()

        //Temp structure
        val tempMessageList = ArrayList<Message>()

        supportActionBar?.title = uName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initDatabase()

        commentViewModel.allCommentsLiveData.observe(this) {

        }

        //Hide default action bar
        //supportActionBar?.hide()

        //Return to Main Activity if backArrow is pressed
        //menuTitle.text = uName
        //backArrow.setOnClickListener {
            //finish()
        //}

        //TEMP FEATURE
        sendButton.setOnClickListener(){
            entry.status = Global.STATUS[1] //status = "snd"
            entry.msgBody = editText.text.toString()
            entry.userID = "1" //Still need to be fixed
            val tsLong = System.currentTimeMillis() / 1000
            entry.timeStamp = tsLong
            if(entry.msgBody != ""){
                tempMessageList.add(entry)
                messageRecyclerView.adapter = MessageViewAdapter(tempMessageList)
                setMessage(entry)
                editText.setText("")
                messageRecyclerView.scrollToPosition(tempMessageList.size - 1)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initDatabase() {
        database = MessageDatabase.getInstance(this)
        databaseDao = database.MessageDatabaseDao
        repository = MessageRepository(databaseDao)
        viewModelFactory = MessageViewModelFactory(repository)
        commentViewModel = ViewModelProvider(this, viewModelFactory).get(
            MessageViewModel.CommentViewModel::class.java)
    }

    private fun setMessage(message: Message) {
        commentViewModel.insert(message)
        Log.e(TAG, "message inserted")
//        val allMessage = commentViewModel.allCommentsLiveData
//        Log.e(TAG, "First message in database: ${allMessage.value?.get(1)?.msgBody}")
    }
}