package com.example.netless_messenger.database

import java.util.*
import androidx.room.*
/* User - data class
*  id - user id, long
*  userName - user name, string
*  deviceName - device name, string
*  deviceID - device UUID, string
* */

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L,

    @ColumnInfo(name = "userName")
    var userName : String = "",

    @ColumnInfo(name = "deviceName")
    var deviceName : String = "",

    @ColumnInfo(name = "deviceID")
    var deviceID : String = ""
)