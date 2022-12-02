package com.example.netless_messenger.ui.main

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.BT_Test
import com.example.netless_messenger.BT_TestViewModel
import com.example.netless_messenger.MainActivity
import com.example.netless_messenger.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel : MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatButton: FloatingActionButton
    private var exampleList: ArrayList<String> = arrayListOf("Jane Doe", "James Doe","Peter Parker")

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View {
        val mainFragmentView =  inflater.inflate(R.layout.fragment_main, container, false)

        floatButton = mainFragmentView.findViewById(R.id.floatingActionButton)
        recyclerView = mainFragmentView.findViewById(R.id.mainFragRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = RecyclerViewAdapter(exampleList)

        //To display custom view for when the recycler view is empty
        val emptyDataObserver = EmptyRecyclerObserver(recyclerView, mainFragmentView.findViewById(R.id.empty_contact_view))
        recyclerView.adapter?.registerAdapterDataObserver(emptyDataObserver)

        //Back arrow does nothing in Main Fragment
        MainActivity.backArrow.setOnClickListener {
        }

        floatButton.setOnClickListener(){

            //Replace main fragment with Add Contact fragment
            val transaction = parentFragmentManager
            transaction.beginTransaction()
                .replace(R.id.container, AddContactFragment())
                .commitNow()
        }

        return mainFragmentView
    }

    override fun onActivityCreated(savedInstanceState : Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

}