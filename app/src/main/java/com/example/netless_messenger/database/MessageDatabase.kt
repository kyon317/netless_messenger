package com.example.netless_messenger.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/* MessageDatabase - An abstract database for message entries */
@Database(entities = [User::class], version = 1)
abstract class MessageDatabase: RoomDatabase() {
    abstract val MessageDatabaseDao: MessageDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE:MessageDatabase?=null

        fun getInstance(context: Context):MessageDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        MessageDatabase::class.java, "message_table").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}