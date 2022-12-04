package com.example.netless_messenger.ui.main

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.DeviceViewModel
import com.example.netless_messenger.MainActivity
import com.example.netless_messenger.R

class AddContactFragment: Fragment() {


    private lateinit var addContactTextView: TextView
    private lateinit var deviceListView: View
    private lateinit var deviceListDialog: AlertDialog
    private lateinit var deviceViewmodel:DeviceViewModel
    private lateinit var discoverySwitch: Switch

    companion object{
        const val RESULT_OK = 90
    }


    // Device View Model initialization
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        deviceViewmodel = MainActivity.deviceViewModel
    }

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
        //Define discovery switch
        discoverySwitch = addContactFragmentView.findViewById(R.id.switch1)
        dicoveryModeSwitch()
        //Create a view from a XML layout
        deviceListView = inflater.inflate(R.layout.add_contact_dialog, null)
        //Set cancel button in the dialog to dismiss the dialog
        val cancelButton = deviceListView.findViewById<Button>(R.id.dialog_cancel_button)
        cancelButton.setOnClickListener {
            deviceListDialog.dismiss()
        }
        val deviceDialogRecyclerView = deviceListView.findViewById<RecyclerView>(R.id.deviceDialogRecyclerView)
        deviceDialogRecyclerView.layoutManager = LinearLayoutManager(activity)
        // initialize DeviceListAdapter
        deviceDialogRecyclerView.adapter = DeviceListAdapter(requireContext(), ArrayList())
        deviceViewmodel.availableDevices.observe(requireActivity(), Observer {
            deviceDialogRecyclerView.adapter = DeviceListAdapter(requireContext(), it)
        })


        //Build the custom alert dialog
        val builder = AlertDialog.Builder(activity)
        builder.setView(deviceListView)
        deviceListDialog = builder.create()

        return addContactFragmentView
    }


    // define action of discovery mode - force the phone to be discoverable
    private fun dicoveryModeSwitch(){
        discoverySwitch.setOnClickListener {
            if (discoverySwitch.isChecked) {
                val requestCode = 1;
                val discoverableIntent: Intent =
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                        putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, RESULT_OK)
                    }

                startActivityForResult(discoverableIntent, requestCode)
                discoverySwitch.isClickable = false

                object : CountDownTimer(RESULT_OK*1000L, 1000) {

                    override fun onTick(millisUntilFinished: Long) {
                    }

                    override fun onFinish() {
                        discoverySwitch.isClickable = true
                        discoverySwitch.isChecked = false
                    }
                }.start()


            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK) print("bluetooth discovery enabled")
        else discoverySwitch.isChecked = false
    }
}