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

        curView.setOnClickListener(){
            if (cur.name!=null) {
                val msgIntent = Intent()
                msgIntent.action = "ATTEMPT_CONNECTION"
                msgIntent.putExtra("SELECTED_DEVICE",cur)
                context.sendBroadcast(msgIntent)
            }
        }
    }

    override fun getItemCount(): Int {
       return deviceNameList.size
    }

    override fun onDetachedFromRecyclerView(recyclerView : RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
//        val bluetoothServicesIntent = Intent(context,ConnectionService::class.java)
//        context.stopService(bluetoothServicesIntent)
    }

}