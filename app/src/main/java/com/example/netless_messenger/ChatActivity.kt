package com.example.netless_messenger

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.database.Message
import com.example.netless_messenger.database.MessageTestViewModel
import com.example.netless_messenger.ui.main.MessageViewAdapter

class ChatActivity: AppCompatActivity() {
    private var entry : Message = Message()
//    private lateinit var database: MessageDatabase
//    private lateinit var databaseDao: MessageDatabaseDao
//    private lateinit var repository: MessageRepository
//    private lateinit var viewModelFactory: MessageViewModelFactory
//    private lateinit var commentViewModel: MessageViewModel.CommentViewModel

    private lateinit var messageTest: MessageTestViewModel

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

        messageTest = ViewModelProvider(this).get(MessageTestViewModel::class.java)

        val uName = intent.getStringExtra("userName").toString()

        supportActionBar?.title = uName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //initDatabase()

        messageTest.allCommentsLiveData.observe(this) {

        //commentViewModel.allCommentsLiveData.observe(this) {
            // show send message history
            messageRecyclerView.adapter = MessageViewAdapter(it as ArrayList<Message>)
            messageRecyclerView.scrollToPosition(it.size - 1)
        }

        sendButton.setOnClickListener{
            entry.status = Global.STATUS[1] //status = "snd"
            entry.msgBody = editText.text.toString()
            //TODO: Implement user ID stuff
            entry.userID = "1" //Still need to be fixed
            val tsLong = System.currentTimeMillis() / 1000
            entry.timeStamp = tsLong
            if(entry.msgBody != ""){
                setMessage(entry)
                editText.setText("")
                sendMessage(entry)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

//    private fun initDatabase() {
//        database = MessageDatabase.getInstance(this)
//        databaseDao = database.MessageDatabaseDao
//        repository = MessageRepository(databaseDao)
//        viewModelFactory = MessageViewModelFactory(repository)
//        commentViewModel = ViewModelProvider(this, viewModelFactory).get(
//            MessageViewModel.CommentViewModel::class.java)
//    }

    private fun setMessage(message: Message) {
        messageTest.insert(message)
        Log.e(TAG, "message inserted")
        val allMessage = messageTest.allCommentsLiveData
//        Log.e(TAG, "First message in database: ${allMessage.value?.get(1)?.msgBody}")
    }




    private fun sendMessage(snd_msg : Message){
        val msgIntent = Intent()
        msgIntent.action = "SENDMSG"
        msgIntent.putExtra("msgBody", snd_msg.msgBody)
        sendBroadcast(msgIntent)
    }
}