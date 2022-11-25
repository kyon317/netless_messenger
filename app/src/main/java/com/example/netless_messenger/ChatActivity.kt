package com.example.netless_messenger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ChatActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity_layout)

        //Hide default action bar
        supportActionBar?.hide()
    }
}