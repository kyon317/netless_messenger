package com.example.netless_messenger

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object Util {
    fun parseTimeStampToDate(timestamp : Long):String{
        val localDate = Date(timestamp)
        val string = SimpleDateFormat("HH:mm").format(localDate)
        return string
    }
}