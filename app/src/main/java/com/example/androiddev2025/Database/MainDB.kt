package com.example.androiddev2025.Database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room


@Database(entities = [Users::class], version = 1, exportSchema = true)
abstract class MainDB : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object{
        fun getDB(context: Context): MainDB{
            return Room.databaseBuilder(
                context.applicationContext,MainDB::class.java, "Users.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}