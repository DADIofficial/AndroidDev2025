package com.example.androiddev2025

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController



class MainActivity : AppCompatActivity() {

        private lateinit var navController: NavController
        private lateinit var bottomNav: BottomNavigationView
        private lateinit var toolBar: View

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.main_activity_layout)

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController

            bottomNav = findViewById(R.id.bottomNav)
            toolBar = findViewById(R.id.toolBar)

            if (Session.isLoggedIn()) switchToMainGraph() else switchToAuthGraph()

            findViewById<TextView>(R.id.LogIn).setOnClickListener {
                if (!Session.isLoggedIn()) navController.navigate(R.id.loginFragment)
            }
            findViewById<TextView>(R.id.Registr).setOnClickListener {
                if (!Session.isLoggedIn()) navController.navigate(R.id.registrFragment)
            }
        }

        fun switchToAuthGraph() {
            navController.setGraph(R.navigation.nav_graph)

            toolBar.visibility = View.VISIBLE
            bottomNav.visibility = View.GONE

            updateToolbar()
        }

        fun switchToMainGraph() {
            navController.setGraph(R.navigation.nav_graph_main)

            toolBar.visibility = View.GONE
            bottomNav.visibility = View.VISIBLE

            bottomNav.setupWithNavController(navController)
        }

        fun updateToolbar() {
            val login = findViewById<TextView>(R.id.LogIn)
            val registr = findViewById<TextView>(R.id.Registr)
            val user = findViewById<TextView>(R.id.User)
            val logout = findViewById<TextView>(R.id.LogOut)

            if (Session.isLoggedIn()) {
                login.visibility = View.GONE
                registr.visibility = View.GONE
                user.visibility = View.VISIBLE
                logout.visibility = View.VISIBLE
                user.text = Session.name ?: "User"
            } else {
                login.visibility = View.VISIBLE
                registr.visibility = View.VISIBLE
                user.visibility = View.GONE
                logout.visibility = View.GONE
            }
        }
    }

