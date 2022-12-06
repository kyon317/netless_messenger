package com.example.netless_messenger.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.ChatActivity
import com.example.netless_messenger.R
import com.example.netless_messenger.database.User

//TODO( "Change exampleList to live list of avaiable devices")
class RecyclerViewAdapter(private val userList : List<User>): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.paired_element,
            parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = userList[position]

        val curView = holder.itemView.findViewById<CardView>(R.id.pairedElement)
        holder.deviceName.text = current.userName

        curView.setOnClickListener{
            val intent = Intent(it.context, ChatActivity::class.java)
            intent.putExtra("contact", current)
            // put chat refrence in intent
            it.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val deviceName = itemView.findViewById<TextView>(R.id.deviceName)
    }


}