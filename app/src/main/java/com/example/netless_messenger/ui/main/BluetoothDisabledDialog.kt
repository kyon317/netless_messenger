package com.example.netless_messenger.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.netless_messenger.R

class BluetoothDisabledDialog : DialogFragment(), DialogInterface.OnClickListener{

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var ret: Dialog
        val builder = AlertDialog.Builder(requireActivity())
        val view: View = requireActivity().layoutInflater.inflate(R.layout.bluetooth_disabled_dialog, null)
        builder.setView(view)
        builder.setTitle("Bluetooth Disabled")
        builder.setPositiveButton("ok", this)
        //builder.setNegativeButton("cancel", this)
        ret = builder.create()
        return ret
    }

    override fun onClick(dialog: DialogInterface, item: Int) {
        requireActivity().finishAffinity()
    }
}