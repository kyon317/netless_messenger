package com.example.netless_messenger

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ChatActivity: AppCompatActivity() {
    private lateinit var backArrow: ImageView

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity_layout)

        //Hide default action bar
        supportActionBar?.hide()

        //Return to Main Activity if backArrow is pressed
        backArrow = findViewById(R.id.back_arrow_icon)
        backArrow.setOnClickListener {
            finish()
        }

    }
}