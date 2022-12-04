package com.example.netless_messenger

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.netless_messenger.database.MessageTestViewModel
import com.example.netless_messenger.ui.main.MainFragment

class MainActivity : AppCompatActivity() {
//    private lateinit var messageTest: MessageTestViewModel

    companion object {
        lateinit var backArrow: ImageView
        lateinit var messageTest: MessageTestViewModel
        lateinit var deviceViewModel:DeviceViewModel
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        deviceViewModel = DeviceViewModel()
        checkPermission()


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

        // initialize Database
        messageTest = ViewModelProvider(this).get(MessageTestViewModel::class.java)

        //To debug ChatActivity.kt
//        val intent = Intent(this, ChatActivity::class.java)
//        startActivity(intent)
    }

    // start bluetooth services
    private fun startServices(){
        val bluetoothServicesIntent = Intent(this,BluetoothServices::class.java)
        startService(bluetoothServicesIntent)
        this.applicationContext.bindService(bluetoothServicesIntent, deviceViewModel, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        val deviceViewModel = DeviceViewModel()
        this.applicationContext.unbindService(deviceViewModel)
    }
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED && (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 10)
        else{
            Log.e(TAG, "Bluetooth permission granted")
            startServices()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Bluetooth permission granted")
                startServices()
            }
        }

    }



}