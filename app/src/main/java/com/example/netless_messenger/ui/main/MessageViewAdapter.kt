package com.example.netless_messenger.ui.main


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.Global
import com.example.netless_messenger.R
import com.example.netless_messenger.database.Message

class MessageViewAdapter(private val messageList: ArrayList<Message>): RecyclerView.Adapter<MessageViewAdapter.MessageViewHolder>() {


    inner class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener{

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.message_element_send_recieve, parent, false))
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        //TODO: Deal with user ID stuff

        val currMessage = messageList[position]

        val send_group = holder.itemView.findViewById<RelativeLayout>(R.id.send_group)
        val send_bubble = holder.itemView.findViewById<TextView>(R.id.send_bubble)

        val receive_group = holder.itemView.findViewById<RelativeLayout>(R.id.receive_group)
        val receive_bubble = holder.itemView.findViewById<TextView>(R.id.receive_bubble)

        if (currMessage.status == Global.STATUS[0]) { //status = "rcv"
            send_group.visibility = View.GONE
            receive_bubble.text = currMessage.msgBody
        } else { //status = "snd"
            receive_group.visibility = View.GONE
            send_bubble.text = currMessage.msgBody
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}