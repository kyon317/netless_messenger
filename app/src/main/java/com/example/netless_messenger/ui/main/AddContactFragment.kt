package com.example.netless_messenger.ui.main

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.BluetoothServices
import com.example.netless_messenger.DeviceViewModel
import com.example.netless_messenger.MainActivity
import com.example.netless_messenger.R

class AddContactFragment: Fragment() {
    private lateinit var addContactTextView: TextView
    private lateinit var moreInfoTextView: TextView
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

        val bluetoothServicesIntent = Intent(requireContext(), BluetoothServices::class.java)

        //Set Add Contact Text View to show Device List Dialog if pressed
        addContactTextView = addContactFragmentView.findViewById(R.id.add_contacts_tv)
        addContactTextView.setOnClickListener {
            //Start discovery service
            requireActivity().startService(bluetoothServicesIntent)
            requireActivity().applicationContext.bindService(bluetoothServicesIntent,
                MainActivity.deviceViewModel, Context.BIND_AUTO_CREATE)

            //Show dialog
            deviceListDialog.show()
            //Set the size of the dialog window
            deviceListDialog.window?.setLayout(800,1000)

        }
        moreInfoTextView = addContactFragmentView.findViewById(R.id.moreinfo_chat_tv)
        moreInfoTextView.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kyon317/netless_messenger/"))
            startActivity(browserIntent)
        }


//        //Define discovery switch
//        discoverySwitch = addContactFragmentView.findViewById(R.id.switch1)
//        dicoveryModeSwitch()

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
        println("Dialog Recycler view size: ${deviceDialogRecyclerView.adapter!!.itemCount}")
        var emptyDataObserver = EmptyRecyclerObserver(deviceDialogRecyclerView, deviceListView.findViewById(R.id.empty_device_view))

        deviceViewmodel.availableDevices.observe(requireActivity(), Observer {
            deviceDialogRecyclerView.adapter = DeviceListAdapter(requireContext(), it)
            //To display custom view for when the recycler view is empty
            emptyDataObserver = EmptyRecyclerObserver(deviceDialogRecyclerView, deviceListView.findViewById(R.id.empty_device_view))
        })

        //Build the custom alert dialog
        val builder = AlertDialog.Builder(activity)
        builder.setView(deviceListView)
        deviceListDialog = builder.create()

        //When dialog is dismissed, stop service
        deviceListDialog.setOnDismissListener {
            requireActivity().stopService(bluetoothServicesIntent)
            requireActivity().applicationContext.unbindService(MainActivity.deviceViewModel)

            val deviceDialogIntent = Intent()
            deviceDialogIntent.action = "CLOSE_DEVICE_DIALOG"
            requireActivity().applicationContext.sendBroadcast(deviceDialogIntent)
        }

        val deviceDialogSuccessFilter = IntentFilter("CONNECTION_SUCCESSFUL")
        requireActivity().applicationContext.registerReceiver(broadcastReceiver, deviceDialogSuccessFilter)

        return addContactFragmentView
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "CONNECTION_SUCCESSFUL") {
                deviceListDialog.dismiss()
                Toast.makeText(requireActivity(), "Connection Successful!", Toast.LENGTH_SHORT).show()
            }

        }
    }


//    // define action of discovery mode - force the phone to be discoverable
//    private fun dicoveryModeSwitch(){
//        discoverySwitch.setOnClickListener {
//            if (discoverySwitch.isChecked) {
//                val requestCode = 1;
//                val discoverableIntent: Intent =
//                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
//                        putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, RESULT_OK)
//                    }
//
//                startActivityForResult(discoverableIntent, requestCode)
//                discoverySwitch.isClickable = false
//
//                object : CountDownTimer(RESULT_OK*1000L, 1000) {
//
//                    override fun onTick(millisUntilFinished: Long) {
//                    }
//
//                    override fun onFinish() {
//                        discoverySwitch.isClickable = true
//                        discoverySwitch.isChecked = false
//                    }
//                }.start()
//
//
//            }
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if(resultCode == RESULT_OK) print("bluetooth discovery enabled")
//        else discoverySwitch.isChecked = false
//    }
}