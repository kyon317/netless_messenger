package com.example.netless_messenger.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/* MessageDatabaseDao - DAO object for MessageDatabase*/
@Dao
interface MessageDatabaseDao {
    @Insert
    suspend fun insertEntry(message : Message)

    //A Flow is an async sequence of values
    //Flow produces values one at a time (instead of all at once) that can generate values
    //from async operations like network requests, database calls, or other async code.
    //It supports coroutines throughout its API, so you can transform a flow using coroutines as well!
    //Code inside the flow { ... } builder block can suspend. So the function is no longer marked with suspend modifier.
    //See more details here: https://kotlinlang.org/docs/flow.html#flows
    @Query("SELECT * FROM message_table")
    fun getAllEntries(): Flow<List<Message>>

    @Query("DELETE FROM message_table")
    suspend fun deleteAll()

    @Query("DELETE FROM message_table WHERE userID = :userID")
    suspend fun deleteUserMessage(userID: String)

    @Query("DELETE FROM message_table WHERE id = :key") //":" indicates that it is a Bind variable
    suspend fun deleteMessage(key: Long)

    @Query("SELECT * FROM message_table WHERE userID = :userID")
    fun getUserMessageEntries(userID: String): List<Message>

}