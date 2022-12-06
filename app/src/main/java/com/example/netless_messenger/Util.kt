package com.example.netless_messenger

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object Util {
    fun parseTimeStampToDate(timestamp : Long):Date{
        return Date(timestamp)
    }
}