package com.example.netless_messenger.ui.main

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.example.netless_messenger.ChatActivity
import com.example.netless_messenger.MainActivity
import com.example.netless_messenger.R
import com.example.netless_messenger.database.Message
import com.example.netless_messenger.database.MessageTestViewModel
import com.example.netless_messenger.database.User
import com.example.netless_messenger.database.UserTestViewModel
import kotlin.jvm.internal.Intrinsics


class UserProfileActivity : AppCompatActivity() {
    private var imageArray = ArrayList<Int>()

    private lateinit var deleteButton: ImageButton
    private lateinit var displayImage: ImageView
    private lateinit var editprofileButton: Button
    private lateinit var contactName: EditText
    private lateinit var editContactNameButton: ImageButton
    private lateinit var deviceName: EditText

    //Image Selection Grid
    private lateinit var imageGridLinearLayout: LinearLayout
    private lateinit var gridView: GridView

    private lateinit var userViewModel: UserTestViewModel
    private lateinit var messageViewModel: MessageTestViewModel
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button

    private var changeAvatarClicked = false
    private var changeContactNameClicke = false

    private lateinit var contact:User
    private var position = -1
    private var avatarRef: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        position = intent.getIntExtra("position", -1)
        userViewModel = ViewModelProvider(this).get(UserTestViewModel::class.java)
        userViewModel.allUsersLiveData.observe(this){

        }

        messageViewModel = ViewModelProvider(this).get(MessageTestViewModel::class.java)
        messageViewModel.allMessageLiveData.observe(this){

        }
        initUiElements()

        populateArray()

        // contact initialization
        contact = intent.getSerializableExtra("contactProfile") as User

        contactName.setText(contact.userName)
        contactName.isEnabled = false

        deviceName.setText(contact.deviceName)
        deviceName.isEnabled = false

        displayImage.setImageResource(contact.userAvatar)


        imageGridLinearLayout.visibility = View.GONE

        val adapter = GridAdapter(this, imageArray)
        gridView.adapter = adapter

        editprofileButton.setOnClickListener(){
            if(!changeAvatarClicked){
                imageGridLinearLayout.visibility = View.VISIBLE
                editprofileButton.text = "DONE"
                changeAvatarClicked = true
            }else{
                imageGridLinearLayout.visibility = View.GONE
                editprofileButton.text = "CHANGE AVATAR"
                changeAvatarClicked = false
            }
        }


        gridView.setOnItemClickListener{adapterView, view, position, l ->
            displayImage.setImageResource(imageArray[position])
            avatarRef = imageArray[position]
        }

        editContactNameButton.setOnClickListener(){
            if(changeContactNameClicke == false){
                contactName.isEnabled = true
                editContactNameButton.setImageResource(R.drawable.ic_baseline_check_24)
                changeContactNameClicke = true
            }else{
                contactName.isEnabled = false
                editContactNameButton.setImageResource(R.drawable.ic_baseline_edit_24)
                changeContactNameClicke = false
            }

        }

        deleteButton.setOnClickListener(){
            //TODO: Delete messages for user from Message DB
            //TODO: Delete contact from user DB

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Request")
            builder.setMessage("Chats from ${contact.userName} as well as their contact information would be lost permanently. Are you sure you wnt to go ahead?" )
            builder.setPositiveButton("OK") { dialog, which ->
                Toast.makeText(this, "OK Pressed", Toast.LENGTH_SHORT).show()

                deleteUser(contact)
                val intentMainFragment = Intent(this, MainActivity::class.java)
                intentMainFragment.putExtra("frag", "mainFragment")
                intentMainFragment.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intentMainFragment)
//                val intentChatFragment = Intent(this, ChatActivity::class.java)
//                intentChatFragment.putExtra("frag", "chatFragment")
//                startActivity(intentChatFragment)
                finish()
            }

            builder.setNegativeButton("CANCEL") { dialog, which ->
                Toast.makeText(this, "Cancel Pressed", Toast.LENGTH_SHORT).show()
            }
            builder.show()

        }
        saveButton.setOnClickListener(){
            editUserDatabase()
            finish()
        }

        cancelButton.setOnClickListener(){
            finish()
        }

    }
    private fun initUiElements(){
        //Initialise UI elements
        deleteButton = findViewById(R.id.delete_profile)
        displayImage = findViewById(R.id.chat_profile_image)
        editprofileButton = findViewById(R.id.edit_avatar)
        contactName = findViewById(R.id.contact_name)
        editContactNameButton = findViewById(R.id.edit_contact_name)
        deviceName = findViewById(R.id.device_name)
        imageGridLinearLayout = findViewById(R.id.grid_layout_older)
        gridView = findViewById(R.id.grid)
        cancelButton = findViewById(R.id.cancel_button)
        saveButton = findViewById(R.id.save_button)

    }

    private fun populateArray(){
        //Populate array
        imageArray.add(R.drawable.avatar_1)
        imageArray.add(R.drawable.avatar_2)
        imageArray.add(R.drawable.avatar_3)
        imageArray.add(R.drawable.avatar_4)
        imageArray.add(R.drawable.avatar_5)
        imageArray.add(R.drawable.avatar_6)
        imageArray.add(R.drawable.avatar_7)
        imageArray.add(R.drawable.avatar_8)
    }


    private fun deleteUser(_contact: User){
        userViewModel.deleteUserById(_contact.id)
        deleteMessage(_contact)
    }

    private fun deleteMessage(_contact: User){
        messageViewModel.deleteUserMessages(_contact.deviceMAC)
    }

    private fun editUserDatabase(){
        val currentUserName = contactName.text.toString()
        var currentAvatar = contact.userAvatar
        if (avatarRef != null)
        {
            currentAvatar = avatarRef as Int
        }

        userViewModel.updateUserNameAndAvatarById(contact.id,currentUserName,currentAvatar)

    }

}