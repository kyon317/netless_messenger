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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.BT_Test
import com.example.netless_messenger.BT_TestViewModel
import com.example.netless_messenger.MainActivity
import com.example.netless_messenger.R
import com.example.netless_messenger.database.MessageTestViewModel

class AddContactFragment: Fragment() {


    private lateinit var addContactTextView: TextView
    private lateinit var deviceListView: View
    private lateinit var deviceListDialog: AlertDialog
    private lateinit var btViewModel: BT_TestViewModel
    private lateinit var btInstance: BT_Test
    private lateinit var discoverySwitch: Switch

    companion object{
        const val RESULT_OK = 90
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





        discoverySwitch = addContactFragmentView.findViewById(R.id.switch1)
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
        val deviceDialogRecyclerView = deviceListView.findViewById<RecyclerView>(R.id.deviceDialogRecyclerView)
        btViewModel = ViewModelProvider(requireActivity()).get(BT_TestViewModel::class.java)
        //val btActiveDeviceList = btViewMode.retrieveDeviceNames(requireContext())
        deviceDialogRecyclerView.layoutManager = LinearLayoutManager(activity)
        //deviceDialogRecyclerView.adapter = DeviceListAdapter(requireContext(), btActiveDeviceList)

        btViewModel.availableDevices.observe(viewLifecycleOwner, Observer {
            val activeList = btViewModel.retrieveDeviceNames(requireContext())
            deviceDialogRecyclerView.adapter = DeviceListAdapter(requireContext(), activeList, this)
        })

        btInstance = BT_Test(requireActivity(), requireContext(), btViewModel)




        //Build the custom alert dialog
        val builder = AlertDialog.Builder(activity)
        builder.setView(deviceListView)
        deviceListDialog = builder.create()

        return addContactFragmentView
    }

    fun getBtInstance(): BT_Test{
        return btInstance
    }

    fun getBtViewModel(): BT_TestViewModel{
        return btViewModel
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK) print("bluetooth discovery enabled")
        else discoverySwitch.isChecked = false
    }
}