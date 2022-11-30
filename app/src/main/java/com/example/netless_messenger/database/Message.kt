package com.example.netless_messenger.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_table")
data class Message(
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L,

    @ColumnInfo(name = "timeStamp")
    var timeStamp : Long = 0L,

    @ColumnInfo(name = "msgBody")
    var msgBody : String = "",

    @ColumnInfo(name = "userID")
    var userID : String = "",

    @ColumnInfo(name = "status")
    var status : String = "",

    @ColumnInfo(name = "msgType")
    var msgType : String = "Text"

)
