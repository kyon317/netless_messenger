package com.example.netless_messenger.ui.main

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.*

class DeviceListAdapter(private val _context: Context, private val deviceNameList: ArrayList<BluetoothDevice>): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    val context : Context = _context.applicationContext
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.MyViewHolder {
        val deviceListView = LayoutInflater.from(parent.context).inflate(
            R.layout.device_list_single,
            parent, false)

        return RecyclerViewAdapter.MyViewHolder(deviceListView)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: RecyclerViewAdapter.MyViewHolder, position: Int) {
        val cur = deviceNameList[position]
        val curView = holder.itemView.findViewById<CardView>(R.id.device_name_single)
        val deviceName = holder.itemView.findViewById<TextView>(R.id.device_name)

        if (cur.name!=null){
            deviceName.text = cur.name.toString()
            Log.e(TAG, "onBindViewHolder: ${cur.name}", )
        }
           
        // TODO: start connection when clicked on a device
        curView.setOnClickListener(){
            if (cur.name!=null) {
                startBluetoothServices(cur)
                startConnectionServices(cur)
            }
//            val btInstance = fragment.getBtInstance()
//            val device = fragment.getBtViewModel().retrieveDevice(cur)
//            btInstance.attemptConnection(device)
        }
    }

    override fun getItemCount(): Int {
       return deviceNameList.size
    }

    // start bluetooth services
    private fun startBluetoothServices(targetDevice : BluetoothDevice){
        val bluetoothServicesIntent = Intent(context,BluetoothServices::class.java)
        bluetoothServicesIntent.putExtra("Device",targetDevice)
        context.startService(bluetoothServicesIntent)
        context.bindService(bluetoothServicesIntent,
            MainActivity.deviceViewModel, Context.BIND_AUTO_CREATE)

    }
    // start bluetooth services
    private fun startConnectionServices(targetDevice : BluetoothDevice){
        val connectionServicesIntent = Intent(context,ConnectionService::class.java)
        connectionServicesIntent.putExtra("Device",targetDevice)
        context.startService(connectionServicesIntent)
        context.bindService(connectionServicesIntent,
            MainActivity.chatViewModel, Context.BIND_AUTO_CREATE)

    }

    override fun onDetachedFromRecyclerView(recyclerView : RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
//        val bluetoothServicesIntent = Intent(context,ConnectionService::class.java)
//        context.stopService(bluetoothServicesIntent)
    }

}