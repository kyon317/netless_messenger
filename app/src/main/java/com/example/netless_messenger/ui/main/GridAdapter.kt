package com.example.netless_messenger.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.netless_messenger.R

class GridAdapter: BaseAdapter{
    var imageList = ArrayList<Int>()
    var context: Context? = null

    constructor(context: Context?, imageList: ArrayList<Int> ): super() {
        this.imageList = imageList
        this.context = context
    }


    override fun getCount(): Int {
        return imageList.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var imgId = this.imageList[p0]
        var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var gView:View = inflater.inflate(R.layout.image_select_element,null)
        var imgView = gView.findViewById<ImageView>(R.id.image_element)
        imgView.setImageResource(imgId)
        return gView
    }
}