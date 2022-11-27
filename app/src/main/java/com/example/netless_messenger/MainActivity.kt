package com.example.netless_messenger

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.netless_messenger.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var backArrow: ImageView
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions(this)

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

        //To debug ChatActivity.kt
//        val intent = Intent(this, ChatActivity::class.java)
//        startActivity(intent)
    }

    fun checkPermissions(activity: Activity)
    {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT), 0)
        }
    }
}