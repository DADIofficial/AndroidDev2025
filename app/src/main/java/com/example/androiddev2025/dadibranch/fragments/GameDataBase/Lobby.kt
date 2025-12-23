package com.example.androiddev2025.dadibranch.fragments.GameDataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lobby")
data class Lobby(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val hostId: Int,

    val maxPlayers: Int,

    val playersIds: String,

    val balanceMin: Int,

    val balanceMax: Int
)



