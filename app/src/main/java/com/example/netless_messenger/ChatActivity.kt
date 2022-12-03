package com.example.netless_messenger

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.database.Message
import com.example.netless_messenger.ui.main.MessageViewAdapter

class ChatActivity: AppCompatActivity() {

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


        //Hide default action bar
        //supportActionBar?.hide()

        //Return to Main Activity if backArrow is pressed
        //menuTitle.text = uName
        //backArrow.setOnClickListener {
            //finish()
        //}

        //TEMP FEATURE
        sendButton.setOnClickListener(){
            val message = Message()
            message.status = Global.STATUS[1] //status = "snd"
            message.msgBody = editText.text.toString()
            //message.timestamp
            //message.userid
            if(message.msgBody != ""){
                tempMessageList.add(message)
                messageRecyclerView.adapter = MessageViewAdapter(tempMessageList)
                editText.setText("")
                messageRecyclerView.scrollToPosition(tempMessageList.size - 1)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}