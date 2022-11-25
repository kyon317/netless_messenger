package com.example.netless_messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.netless_messenger.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var backArrow: ImageView
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Hide default action bar
        supportActionBar?.hide()

        //Initialize back arrow
        backArrow = findViewById(R.id.back_arrow_icon)

        //Add main fragment into the container
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}