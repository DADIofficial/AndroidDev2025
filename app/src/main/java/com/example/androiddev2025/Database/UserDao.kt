package com.example.androiddev2025.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(item : Users)

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<Users>>

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()


    @Query("SELECT * FROM user")
    suspend fun getAllUsersOnce(): List<Users>

    @Insert
    suspend fun insertAll(list: List<Users>)

//    @Update
//    suspend fun updateUser(user: Users)

}