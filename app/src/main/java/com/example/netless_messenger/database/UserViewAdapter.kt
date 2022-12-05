package com.example.netless_messenger.database

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import com.example.netless_messenger.R

/* View adapter from User database entry */
class UserViewAdapter (private val context: Context, private var entryList: List<User>):
//TODO: Redundant code - Delete maybe
    BaseAdapter() {
    override fun getCount() : Int {
        return entryList.size
    }

    override fun getItem(position : Int) : Any {
        return entryList[position]
    }

    override fun getItemId(position : Int) : Long {
        return position.toLong()
    }

    override fun getView(position : Int, convertView : View?, parent : ViewGroup?) : View {
        val view:View = View.inflate(context, R.layout.user_dummy,null)
        val UserNameInput = view.findViewById<EditText>(R.id.user_name_input)
        val DeviceNameInput = view.findViewById<TextView>(R.id.device_name_input)
        val entry = entryList.get(position)

        // put values from entry
        UserNameInput.setText(entry.userName)
        DeviceNameInput.text = entry.deviceName

        return view
    }

    fun replace(newList: List<User>){
        entryList = newList
    }
}