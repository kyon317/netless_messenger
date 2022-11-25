package com.example.netless_messenger.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.netless_messenger.MainActivity
import com.example.netless_messenger.R

class AddContactFragment: Fragment() {

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

        return addContactFragmentView
    }
}