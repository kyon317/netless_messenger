package com.example.netless_messenger

import android.bluetooth.BluetoothManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.database.Message
import com.example.netless_messenger.database.MessageTestViewModel
import com.example.netless_messenger.database.User
import com.example.netless_messenger.database.UserTestViewModel
import com.example.netless_messenger.ui.main.UserProfileActivity
import com.example.netless_messenger.ui.main.MessageViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ChatActivity: AppCompatActivity() {
    private var entry : Message = Message()

    private lateinit var userViewModel: UserTestViewModel
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
    private var isConnected:Boolean? = false
    private var currentActiveDeviceAddress:String? = ""

    companion object{
        private val AVAILABLE = Color.GREEN            //#00FF00 //Green
        private val UNAVAILABLE = Color.RED           //#FF0000 //Red
        private val AVALIABLE_STATUS = "Available"
        private val UNAVALIABLE_STATUS = "Unavailable"
    }

    //TODO: Delete Message functionality
    //TODO: Reconnect Button Functionality

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



        //Define reset button
        reset_connection_button.setOnClickListener(){
            Toast.makeText(this, "Attempting connection", Toast.LENGTH_SHORT).show()
            val pendingIntent = Intent()
            val killIntent = Intent()
            killIntent.action = "KILL_CONNECTION"
            if (status.text == UNAVALIABLE_STATUS){
                sendBroadcast(killIntent)
                pendingIntent.action = "ATTEMPT_CONNECTION"
                val btManager = this.applicationContext?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
                val bluetoothAdapter = btManager?.adapter
                val targetDevice = bluetoothAdapter?.getRemoteDevice(incomingContact.deviceMAC)
                pendingIntent.putExtra("SELECTED_DEVICE",targetDevice)
                sendBroadcast(pendingIntent)
            }else
            {
                Toast.makeText(this, "You are Connected!", Toast.LENGTH_SHORT).show()
            }
        }

        messageTest = ViewModelProvider(this).get(MessageTestViewModel::class.java)
        val chatViewModel = MainActivity.chatViewModel
        chatViewModel.currentMessageList.observe(this, Observer {
            Log.e(TAG, "onCreate: current message list size is ${it.size}" )
        })

        //ActionBar operations
        incomingContact = intent.getSerializableExtra("contact") as User
        val contactName = incomingContact.userName
        val contactDeviceMac = incomingContact.deviceMAC
        display_image.setImageResource((incomingContact.userAvatar))

        //Initialize User Database
        initUserDatabase()

        //Initialise Status onCrete
        if(chatViewModel.deviceAddress.value == contactDeviceMac){
            status.text = AVALIABLE_STATUS
            status.background.setTint(AVAILABLE)
        }
        else{
            status.text = UNAVALIABLE_STATUS
            status.background.setTint(UNAVAILABLE)
        }


        chatViewModel.isConnectedToDevice.observe(this){
            //Status Background Color needs to change
            //Status text == Available
            isConnected = it
            if (it && currentActiveDeviceAddress == incomingContact.deviceMAC ){
                status.text = AVALIABLE_STATUS
                status.background.setTint(AVAILABLE)
                reset_connection_button.visibility = View.GONE
            }
            else{
                status.text = UNAVALIABLE_STATUS
                status.background.setTint(UNAVAILABLE)
                reset_connection_button.visibility = View.VISIBLE
            }
        }

        chatViewModel.deviceAddress.observe(this){
            //Status Background Color needs to change
            //Status text == Available
            currentActiveDeviceAddress = it
            if(it == contactDeviceMac && isConnected == true){
                status.text = AVALIABLE_STATUS
                status.background.setTint(AVAILABLE)
                reset_connection_button.visibility = View.GONE
            }
            else{
                status.text = UNAVALIABLE_STATUS
                status.background.setTint(UNAVAILABLE)
                reset_connection_button.visibility = View.VISIBLE
            }
        }

        //Hide Action Bar
        supportActionBar?.hide()

        //Set user name as title
        user_name_appbar.text = contactName

        user_name_appbar.setOnClickListener(){
            val position = intent.getIntExtra("position",-1)

            val profileIntent = Intent(this, UserProfileActivity::class.java)
            profileIntent.putExtra("contactProfile", incomingContact)
            if (position != -1){
                profileIntent.putExtra("position",position)
            }
            startActivity(profileIntent)
        }

        //Back Button Action
        back_button.setOnClickListener(){
            finish()
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
        //Checking connection Running Status
        chatViewModel.isConnectionServiceRunning.observe(this){
            if(!it){
                val connectionServicesIntent = Intent(this,ConnectionService::class.java)
                stopService(connectionServicesIntent)
                applicationContext.unbindService(MainActivity.chatViewModel)

                chatViewModel.resetFlag_isConnectionServiceRunning()
            }
        }
    }


    private fun initUserDatabase(){
        userViewModel = ViewModelProvider(this).get(UserTestViewModel::class.java)
        userViewModel.allUsersLiveData.observe(this){
        }
    }

    private fun setMessage(message: Message) {
        message.timeStamp = System.currentTimeMillis()
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

    override fun onResume() {
        super.onResume()
        val dummy = intent.getStringExtra("frag")
        if (dummy == "chatFragment"){
            finish()
        }
    }
}