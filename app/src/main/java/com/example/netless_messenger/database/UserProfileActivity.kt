package com.example.netless_messenger.database

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.example.netless_messenger.R
import com.example.netless_messenger.ui.main.GridAdapter


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
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button

    private var changeAvatarClicked = false
    private var changeContactNameClicke = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        userViewModel = ViewModelProvider(this).get(UserTestViewModel::class.java)
        initUiElements()

        populateArray()

        val contact = intent.getSerializableExtra("contactProfile") as User

        contactName.setText(contact.userName)
        contactName.isEnabled = false

        deviceName.setText(contact.deviceName)
        deviceName.isEnabled = false

        imageGridLinearLayout.visibility = View.GONE

        val adapter = GridAdapter(this, imageArray)
        gridView.adapter = adapter

        editprofileButton.setOnClickListener(){
            if(changeAvatarClicked == false){
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
        }

        editContactNameButton.setOnClickListener(){
            if(changeContactNameClicke == false){
                contactName.isEnabled = true
                editContactNameButton.setImageResource(R.drawable.ic_baseline_check_box_24)
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
}