package com.example.androiddev2025.dadibranch.fragments.GameDataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LobbyDao {

    @Insert
    suspend fun insert(lobby: Lobby)

    @Query("SELECT * FROM lobby")
    suspend fun getAll(): List<Lobby>

    @Query("SELECT * FROM lobby WHERE id = :id")
    suspend fun getById(id: Int): Lobby

    @Query("UPDATE lobby SET playersIds = :players WHERE id = :id")
    suspend fun updatePlayers(id: Int, players: String)

    @Query("DELETE FROM Lobby WHERE id = :id")
    suspend fun deleteById(id: Int)
}

