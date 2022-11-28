package com.example.netless_messenger.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.R

class DeviceListAdapter(private val context: Context, private val deviceNameList: ArrayList<String>): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.MyViewHolder {
        val deviceListView = LayoutInflater.from(parent.context).inflate(
            R.layout.device_list_single,
            parent, false)

        return RecyclerViewAdapter.MyViewHolder(deviceListView)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.MyViewHolder, position: Int) {
        val cur = deviceNameList[position]
        val curView = holder.itemView.findViewById<CardView>(R.id.device_name_single)
        val deviceName = holder.itemView.findViewById<TextView>(R.id.device_name)

        deviceName.text = cur
        curView.setOnClickListener(){
            //TODO: Add Connectivity features here

        }
    }

    override fun getItemCount(): Int {
       return deviceNameList.size
    }


}