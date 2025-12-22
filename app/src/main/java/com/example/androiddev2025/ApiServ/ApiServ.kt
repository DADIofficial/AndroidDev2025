package com.example.androiddev2025.ApiServ

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServ {
    @GET("users")
    suspend fun getUsers(@Query("limit") limit: Int): UserList
}