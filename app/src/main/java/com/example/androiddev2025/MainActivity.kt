package com.example.androiddev2025

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        val MainPage = findViewById<TextView>(R.id.Mainlogo)
        val LoginPage = findViewById<TextView>(R.id.LogIn)
        val RegistPage = findViewById<TextView>(R.id.Registr)

        MainPage.setOnClickListener {
            navController.navigate(R.id.mainFragment)
        }

        LoginPage.setOnClickListener {
            navController.navigate(R.id.loginFragment)
        }

        RegistPage.setOnClickListener {
            navController.navigate(R.id.registrFragment)
        }
    }
}
