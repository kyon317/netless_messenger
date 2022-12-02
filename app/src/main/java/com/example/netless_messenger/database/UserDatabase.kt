package com.example.jiaqing_hu.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.netless_messenger.database.User

/* UserDatabase - An abstract database for entries */
@Database(entities = [User::class], version = 1)
abstract class UserDatabase: RoomDatabase() {
    abstract val userDatabaseDao:UserDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE:UserDatabase?=null

        fun getInstance(context: Context):UserDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        UserDatabase::class.java, "user_table").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}