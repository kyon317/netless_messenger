package com.example.jiaqing_hu.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.netless_messenger.R
import com.example.netless_messenger.database.User
import kotlinx.coroutines.flow.Flow

/* UserDatabaseDao - DAO object for UserDatabase*/
@Dao
interface UserDatabaseDao {
    @Insert
    suspend fun insertEntry(user: User)

    //A Flow is an async sequence of values
    //Flow produces values one at a time (instead of all at once) that can generate values
    //from async operations like network requests, database calls, or other async code.
    //It supports coroutines throughout its API, so you can transform a flow using coroutines as well!
    //Code inside the flow { ... } builder block can suspend. So the function is no longer marked with suspend modifier.
    //See more details here: https://kotlinlang.org/docs/flow.html#flows
    @Query("SELECT * FROM user_table")
    fun getAllEntries(): Flow<List<User>>

    @Query("DELETE FROM user_table")
    suspend fun deleteAll()

    @Query("DELETE FROM user_table WHERE id = :key") //":" indicates that it is a Bind variable
    suspend fun deleteUser(key: Long)

    @Query("UPDATE user_table SET userName = :userName, userAvatar = :userAvatar WHERE id = :key")
    suspend fun updateUser(key: Long, userName: String, userAvatar: Int)
}
