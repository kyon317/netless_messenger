package com.example.netless_messenger.ui.main


import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.Global
import com.example.netless_messenger.R
import com.example.netless_messenger.Util
import com.example.netless_messenger.database.Message

class MessageViewAdapter(private val context: Context, private val messageList: ArrayList<Message>): RecyclerView.Adapter<MessageViewAdapter.MessageViewHolder>() {


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
        //TODO: Implement user timeStamp conversion to string

        holder.setIsRecyclable(false)

        val currMessage = messageList[position]
        val currentView = holder.itemView.findViewById<RelativeLayout>(R.id.message_holder)

        val send_group = holder.itemView.findViewById<RelativeLayout>(R.id.send_group)
        val send_bubble = holder.itemView.findViewById<TextView>(R.id.send_bubble)
        val send_timestamp = holder.itemView.findViewById<TextView>(R.id.sent_time_stamp)

        val receive_group = holder.itemView.findViewById<RelativeLayout>(R.id.receive_group)
        val receive_bubble = holder.itemView.findViewById<TextView>(R.id.receive_bubble)
        val receive_timestamp = holder.itemView.findViewById<TextView>(R.id.receive_time_stamp)

        if (currMessage.status == Global.STATUS[0]) { //status = "rcv"
            send_group.visibility = View.GONE
            receive_bubble.text = currMessage.msgBody
            receive_timestamp.text = Util.parseTimeStampToDate(currMessage.timeStamp)
        } else { //status = "snd"
            receive_group.visibility = View.GONE
            send_bubble.text = currMessage.msgBody
            send_timestamp.text = Util.parseTimeStampToDate(currMessage.timeStamp)
        }


    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}