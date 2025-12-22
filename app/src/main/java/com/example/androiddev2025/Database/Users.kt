package com.example.androiddev2025.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "user")
data class Users(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "Name")
    var name: String,
    @ColumnInfo(name = "Email")
    var email: String,
    @ColumnInfo(name = "Password")
    var password: String,
    @ColumnInfo(name = "Balance")
    var balance: Float,
    @ColumnInfo(name = "Admin")
    var admin: Boolean,

)
