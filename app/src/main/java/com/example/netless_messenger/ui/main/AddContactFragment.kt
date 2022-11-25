package com.example.netless_messenger.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.netless_messenger.MainActivity
import com.example.netless_messenger.R

class AddContactFragment: Fragment() {

    private lateinit var addContactTextView: TextView
    private lateinit var deviceListView: View
    private lateinit var deviceListDialog: AlertDialog

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View {
        val addContactFragmentView =  inflater.inflate(R.layout.fragment_add_contact, container, false)

        //Back arrow go back to Main Fragment
        MainActivity.backArrow.setOnClickListener {
            //Replace current fragment with MainFragment
            val transaction = parentFragmentManager
            transaction.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        //Set Add Contact Text View to show Device List Dialog if pressed
        addContactTextView = addContactFragmentView.findViewById(R.id.add_contacts_tv)
        addContactTextView.setOnClickListener {
            deviceListDialog.show()
            //Set the size of the dialog window
            deviceListDialog.window?.setLayout(800,1000)
        }

        //Create a view from a XML layout
        deviceListView = inflater.inflate(R.layout.add_contact_dialog, null)
        //Set cancel button in the dialog to dismiss the dialog
        val cancelButton = deviceListView.findViewById<Button>(R.id.dialog_cancel_button)
        cancelButton.setOnClickListener {
            deviceListDialog.dismiss()
        }

        //Build the custom alert dialog
        val builder = AlertDialog.Builder(activity)
        builder.setView(deviceListView)
        deviceListDialog = builder.create()

        return addContactFragmentView
    }
}