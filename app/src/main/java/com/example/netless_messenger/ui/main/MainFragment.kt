package com.example.netless_messenger.ui.main

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netless_messenger.MainActivity
import com.example.netless_messenger.R
import com.example.netless_messenger.database.User
import com.example.netless_messenger.database.UserTestViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        lateinit var userViewModel: UserTestViewModel
    }

    private lateinit var viewModel : MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatButton: FloatingActionButton

    private var exampleList: ArrayList<String> = arrayListOf("Jane Doe", "James Doe","Peter Parker")


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View {
        val mainFragmentView =  inflater.inflate(R.layout.fragment_main, container, false)

        floatButton = mainFragmentView.findViewById(R.id.floatingActionButton)
        recyclerView = mainFragmentView.findViewById(R.id.mainFragRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        //To display custom view for when the recycler view is empty
        var emptyDataObserver = EmptyRecyclerObserver(recyclerView, mainFragmentView.findViewById(R.id.empty_contact_view))

//
        //Initialize User DB
        userViewModel = ViewModelProvider(this.requireActivity()).get(UserTestViewModel::class.java)

//        val tempUser = User(
//            0,
//            "Adam",
//            R.drawable.avatar_2,
//            "Galaxy Note10",
//            "123",
//            "MAC123"
//            )
//        userViewModel.insert(tempUser)

        val insertionFilter = IntentFilter("INSERTION_REQUIRED")
        this.requireActivity().applicationContext.registerReceiver(broadcastReceiver, insertionFilter)
        userViewModel.allUsersLiveData.observe(this.requireActivity()){
            //DO NOTHING
            Log.e(TAG, "onCreateView: current user list size ${it.size}" )
            recyclerView.adapter = RecyclerViewAdapter(it,"00")
            emptyDataObserver = EmptyRecyclerObserver(recyclerView, mainFragmentView.findViewById(R.id.empty_contact_view))
        }
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

    private fun onNewUserConnected(newUser : User){
        userViewModel.insert(newUser)
        Log.e(TAG, "onNewUserConnected: inserted" )
//        recyclerView.adapter = RecyclerViewAdapter(userList = userViewModel.allUsersLiveData.value!!)
//        userViewModel.allUsersLiveData.observe(this){
//            Log.e(TAG, "onNewUserConnected: list updated ${it.size}" )
//            recyclerView.adapter = RecyclerViewAdapter(it)
//        }
    }

//    override fun onActivityCreated(savedInstanceState : Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

private val broadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == "INSERTION_REQUIRED")
        {
            val contact: User = intent.getSerializableExtra("CONTACT") as User
            onNewUserConnected(contact)
        }
    }
}



}