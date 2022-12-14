package com.example.netless_messenger

import android.Manifest
import android.bluetooth.BluetoothManager
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
import com.example.netless_messenger.ui.main.BluetoothDisabledDialog
import com.example.netless_messenger.ui.main.MainFragment
import kotlin.concurrent.fixedRateTimer

class MainActivity : AppCompatActivity() {
//    private lateinit var messageTest: MessageTestViewModel

    var bluetoothDisabledFlag = false;

    companion object {
        lateinit var backArrow: ImageView
        lateinit var messageTest: MessageTestViewModel
        lateinit var deviceViewModel:DeviceViewModel
        lateinit var chatViewModel:ChatViewModel
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        deviceViewModel = DeviceViewModel()
        chatViewModel = ChatViewModel()
        checkPermission()

        //If bluetooth is disabled, close app on dialog dismiss
        if(!bluetoothDisabledFlag) {
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

        //Checking connection Running Status
        chatViewModel.isConnectionServiceRunning.observe(this){
            if(!it){
                val connectionServicesIntent = Intent(this,ConnectionService::class.java)
                startService(connectionServicesIntent)
                applicationContext.bindService(connectionServicesIntent, MainActivity.chatViewModel, Context.BIND_AUTO_CREATE)


                    chatViewModel.resetFlag_isConnectionServiceRunning()
                }
            }
        }
        //To debug ChatActivity.kt
//        val intent = Intent(this, ChatActivity::class.java)
//        startActivity(intent)
    }

    // start bluetooth services
    private fun startServices(){
        val connectionServicesIntent = Intent(this,ConnectionService::class.java)
        startService(connectionServicesIntent)
        applicationContext.bindService(connectionServicesIntent, chatViewModel, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()

        val flag = intent.getStringExtra("frag")
        if(flag == "mainFragment"){
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(!bluetoothDisabledFlag){
            val connectionServicesIntent = Intent(this,ConnectionService::class.java)
//        connectionServicesIntent.putExtra("Message",ConnectionService.CONNECTION_TERMINATE)
            stopService(connectionServicesIntent)
            applicationContext.unbindService(chatViewModel)
        }
    }

    private fun checkPermission() {
        val btManager = this.applicationContext?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        val btAdapter = btManager?.adapter
        if(!btAdapter?.isEnabled!!)
        {
            val myDialog = BluetoothDisabledDialog()
            myDialog.show(supportFragmentManager, "my dialog")
            bluetoothDisabledFlag = true;
            return;
        }

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