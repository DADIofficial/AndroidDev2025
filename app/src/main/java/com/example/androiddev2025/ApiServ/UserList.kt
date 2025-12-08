package com.example.androiddev2025.ApiServ

data class UserList(
    val users: List<UserData>,
    val total: Int,
    val skip: Int,
    val limit: Int
)