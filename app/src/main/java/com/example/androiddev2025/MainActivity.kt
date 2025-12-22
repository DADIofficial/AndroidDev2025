package com.example.androiddev2025

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.findNavController
import android.content.Context
import android.util.Log


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

        val logout = findViewById<TextView>(R.id.LogOut)

        MainPage.setOnClickListener {
            navController.navigate(R.id.mainFragment)
        }

        LoginPage.setOnClickListener {
            navController.navigate(R.id.loginFragment)
        }

        RegistPage.setOnClickListener {
            navController.navigate(R.id.registrFragment)
        }

        val user = findViewById<TextView>(R.id.User)


        user.setOnClickListener {
            if (Session.admin) {
                navController.navigate(R.id.AdminPageFragment)
            } else {
                navController.navigate(R.id.UserPageFragment)
            }
        }

        logout.setOnClickListener {
            Session.logout()
            updateToolbar()
            navController.navigate(R.id.mainFragment)
        }



    }

    fun updateToolbar() {
        val LoginPage = findViewById<TextView>(R.id.LogIn)
        val RegistPage = findViewById<TextView>(R.id.Registr)
        val user = findViewById<TextView>(R.id.User)
        val logout = findViewById<TextView>(R.id.LogOut)


        if (Session.isLoggedIn()) {
            LoginPage.visibility = View.GONE
            RegistPage.visibility = View.GONE

            user.visibility = View.VISIBLE
            logout.visibility = View.VISIBLE
            user.text = Session.name
        } else {
            LoginPage.visibility = View.VISIBLE
            RegistPage.visibility = View.VISIBLE
            user.visibility = View.GONE
            logout.visibility = View.GONE
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        Session.logout()
    }


}
