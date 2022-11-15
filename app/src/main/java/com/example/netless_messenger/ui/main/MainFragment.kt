package com.example.netless_messenger.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        floatButton.setOnClickListener(){
            Toast.makeText(activity, "Floating Button Clicked", Toast.LENGTH_SHORT).show()
        }
        return mainFragmentView
    }

    override fun onActivityCreated(savedInstanceState : Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }


}