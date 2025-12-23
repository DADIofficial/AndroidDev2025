package com.example.androiddev2025.dadibranch.fragments.GameDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Lobby::class], version = 3, exportSchema = true)
abstract class GameDB : RoomDatabase() {

    abstract fun lobbyDao(): LobbyDao

    companion object {
        fun getDB(context: Context): GameDB {
            return Room.databaseBuilder(
                context.applicationContext,
                GameDB::class.java,
                "Game.db"
            ).fallbackToDestructiveMigration()
                .build()
        }
    }
}
