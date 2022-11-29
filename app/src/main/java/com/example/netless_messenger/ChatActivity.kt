package com.example.netless_messenger

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val tempMessageList = ArrayList<String>()

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
            val message = editText.text.toString()
            if(message != ""){
                tempMessageList.add(message)
                messageRecyclerView.adapter = MessageViewAdapter(tempMessageList)
                editText.setText("")
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}