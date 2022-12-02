package com.example.netless_messenger

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
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
        //Don't use magic numbers for request code
        checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION,1)
        checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION,2)
        checkSinglePermission(Manifest.permission.BLUETOOTH,3)
        checkSinglePermission(Manifest.permission.BLUETOOTH_ADMIN,4)
        checkSinglePermission(Manifest.permission.BLUETOOTH_CONNECT,5)
        checkSinglePermission(Manifest.permission.BLUETOOTH_SCAN,0)
//        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
//            || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//            || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//            || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
//            || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
//            || ContextCompat.checkSelfPermission(activity!!, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(activity, arrayOf(
//                Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
//                , Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT), 0)
//        }
    }

    fun checkSinglePermission(permission: String, requestCode: Int){
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }else {
            Toast.makeText(this@MainActivity, permission+" already granted", Toast.LENGTH_SHORT).show()
        }
    }

    // This function is called when the user accepts or decline the permission.
// Request Code is used to check which permission called this function.
// This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           // Toast.makeText(this@MainActivity, permissions[0]+" Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            if (permissions.isNotEmpty())
            {

            }
           //     Toast.makeText(this@MainActivity, permissions[0]+" Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}