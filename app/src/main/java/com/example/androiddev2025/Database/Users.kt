package com.example.androiddev2025.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "user")
data class Users(
    @PrimaryKey(autoGenerate = true)
    var ID: Int? = null,
    @ColumnInfo(name = "Name")
    var Name: String,
    @ColumnInfo(name = "Email")
    var Email: String,
    @ColumnInfo(name = "Password")
    var Password: String,
    @ColumnInfo(name = "Balance")
    var Balance: Float,
)
