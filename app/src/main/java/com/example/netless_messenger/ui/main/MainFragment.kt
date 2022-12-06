package com.example.netless_messenger.ui.main

import android.os.Bundle
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
    }

    private lateinit var viewModel : MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatButton: FloatingActionButton
    private lateinit var userViewModel: UserTestViewModel

    private var exampleList: ArrayList<String> = arrayListOf("Jane Doe", "James Doe","Peter Parker")


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

        //Initialize User DB
        userViewModel = ViewModelProvider(requireActivity()).get(UserTestViewModel::class.java)


        val tempUser = User(
            0,
            "Adam Smith",
            R.drawable.profile,
            "Nokia 1100",
            "123",
            "MAC123"
        )

//        val user2 = User(
//            1,
//            "Reynolds Ladestu",
//            R.drawable.profile,
//            "Galaxy Note10",
//            "123",
//            "MAC123"
//        )
//
//        userViewModel.insert(tempUser)
//        userViewModel.insert(user2)
//
        val userList = userViewModel.allUsersLiveData.value

        userViewModel.allUsersLiveData.observe(requireActivity()){
            //DO NOTHING
            recyclerView.adapter = RecyclerViewAdapter(it)
            emptyDataObserver = EmptyRecyclerObserver(recyclerView, mainFragmentView.findViewById(R.id.empty_contact_view))
        }


//

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