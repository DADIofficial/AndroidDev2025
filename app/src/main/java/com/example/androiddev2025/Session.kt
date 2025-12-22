package com.example.androiddev2025

object Session {
    var email: String? = null
    var name: String? = null
    var admin: Boolean = false

    var balance: Float = 0f

    fun isLoggedIn() = email != null

    fun login(email: String, name: String, admin: Boolean, balance: Float) {
        this.email = email
        this.name = name
        this.admin = admin
        this.balance = balance
    }

    fun logout() {
        email = null
        name = null
        admin = false
        balance = 0f
    }
}
