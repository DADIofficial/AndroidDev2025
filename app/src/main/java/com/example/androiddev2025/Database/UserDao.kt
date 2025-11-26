package com.example.androiddev2025.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {

    @Insert
    fun insertUser(item : Users)

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<Users>>

    @Query("DELETE FROM user")
    fun deleteAllUsers()
}