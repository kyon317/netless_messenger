package com.example.netless_messenger

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.database.Message
import com.example.netless_messenger.database.MessageTestViewModel
import com.example.netless_messenger.database.User
import com.example.netless_messenger.database.UserProfileActivity
import com.example.netless_messenger.ui.main.MessageViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ChatActivity: AppCompatActivity() {
    private var entry : Message = Message()
//    private lateinit var database: MessageDatabase
//    private lateinit var databaseDao: MessageDatabaseDao
//    private lateinit var repository: MessageRepository
//    private lateinit var viewModelFactory: MessageViewModelFactory
//    private lateinit var commentViewModel: MessageViewModel.CommentViewModel

    private lateinit var messageTest: MessageTestViewModel
    private var messageList: ArrayList<Message> = ArrayList()

    private lateinit var editText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageRecyclerView: RecyclerView

    //Custom AppBar Elements
    private lateinit var back_button: ImageView
    private lateinit var reset_connection_button: ImageView
    private lateinit var display_image: ImageView
    private lateinit var status: TextView
    private lateinit var user_name_appbar: TextView


    //Contact Info
    private lateinit var incomingContact:User

    companion object{
        private val AVAILABLE = Color.GREEN                //#00FF00 //Green
        private val UNAVAILABLE = Color.RED           //#FF0000 //Red
        private val AVALIABLE_STATUS = "Available"
        private val UNAVALIABLE_STATUS = "Unavailable"
    }

    //TODO: Delete Message functionality
    //TODO: Reconnect Button Functionality
    //TODO: Display Image Customisation

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity_layout)
        editText = findViewById(R.id.chat_input_edit_text)
        sendButton = findViewById(R.id.send_button)
        messageRecyclerView = findViewById(R.id.message_list)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)

        //Custom AppBar
        back_button = findViewById(R.id.back_arrow_icon)
        reset_connection_button = findViewById(R.id.reset_bttn)
        display_image = findViewById(R.id.chat_profile_image)
        status = findViewById(R.id.status)
        user_name_appbar = findViewById(R.id.chat_user_name)


        messageTest = ViewModelProvider(this).get(MessageTestViewModel::class.java)
        val chatViewModel = MainActivity.chatViewModel
        chatViewModel.currentMessageList.observe(this, Observer {
            Log.e(TAG, "onCreate: current message list size is ${it.size}" )
        })

        //ActionBar operations
        incomingContact = intent.getSerializableExtra("contact") as User
        val contactName = incomingContact.userName
        val contactDeviceMac = incomingContact.deviceMAC



        //Initialise Status onCrete
        if(chatViewModel.deviceAddress.value == contactDeviceMac){
            status.text = AVALIABLE_STATUS
            status.background.setTint(AVAILABLE)
        }
        else{
            status.text = UNAVALIABLE_STATUS
            status.background.setTint(UNAVAILABLE)
        }

        chatViewModel.deviceAddress.observe(this){
                //Status Background Color needs to change
                //Status text == Available
            if(it == contactDeviceMac){
                status.text = AVALIABLE_STATUS
                status.background.setTint(AVAILABLE)
            }
            else{
                status.text = UNAVALIABLE_STATUS
                status.background.setTint(UNAVAILABLE)
            }
        }

        //Hide Action Bar
        supportActionBar?.hide()

        //Set user name as title
        user_name_appbar.text = contactName

        user_name_appbar.setOnClickListener(){
            val profileIntent = Intent(this, UserProfileActivity::class.java)
            profileIntent.putExtra("contactProfile", incomingContact)
            startActivity(profileIntent)
        }

        //Back Button Action
        back_button.setOnClickListener(){
            finish()
        }

        //Reset Button Action
        reset_connection_button.setOnClickListener(){

        }

        messageTest.allMessageLiveData.observe(this) {
        //commentViewModel.allCommentsLiveData.observe(this) {
            // show send message history
            retrieveUserMessages(incomingContact.deviceMAC)
            Thread.sleep(100)
//            messageRecyclerView.adapter = MessageViewAdapter(it as ArrayList<Message>)
            messageRecyclerView.adapter = MessageViewAdapter(messageList)
            messageRecyclerView.scrollToPosition(messageList.size - 1)

        }


        sendButton.setOnClickListener{
            entry.status = Global.STATUS[1] //status = "snd"
            entry.msgBody = editText.text.toString()
            //TODO: Implement user ID stuff
            entry.userID = incomingContact.deviceMAC //Still need to be fixed
            val tsLong = System.currentTimeMillis() / 1000
            entry.timeStamp = tsLong
            if(entry.msgBody != ""){
                setMessage(entry)
                editText.setText("")
                sendMessage(entry)
            }
        }
        //Setting Status

    }


//    private fun initDatabase() {
//        database = MessageDatabase.getInstance(this)
//        databaseDao = database.MessageDatabaseDao
//        repository = MessageRepository(databaseDao)
//        viewModelFactory = MessageViewModelFactory(repository)
//        commentViewModel = ViewModelProvider(this, viewModelFactory).get(
//            MessageViewModel.CommentViewModel::class.java)
//    }

    private fun setMessage(message: Message) {
        messageTest.insert(message)
        Log.e(TAG, "message inserted")
        val allMessage = messageTest.allMessageLiveData
//        Log.e(TAG, "First message in database: ${allMessage.value?.get(1)?.msgBody}")
    }

    //This function will update the adapter with message list of the specified user
    private fun retrieveUserMessages(userId: String) {
        CoroutineScope(IO).launch {
            var currentMessageList = messageTest.getUserMessageEntries(userId)
            if (currentMessageList.isNotEmpty())
                messageList = currentMessageList as ArrayList<Message>
        }
    }


    private fun sendMessage(snd_msg : Message){
        val msgIntent = Intent()
        msgIntent.action = "SENDMSG"
        msgIntent.putExtra("msgBody", snd_msg.msgBody)
        sendBroadcast(msgIntent)
    }
}