package com.example.androiddev2025

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androiddev2025.Database.MainDB
import com.example.androiddev2025.Database.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.Observer
import androidx.lifecycle.LiveData

class Registr_page_frag: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.registr_page_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val login = view.findViewById<TextView>(R.id.LogInR)

        val button = view.findViewById<Button>(R.id.RegistrButton)

        login.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        button.setOnClickListener {

            Toast.makeText(requireContext(), "тест", Toast.LENGTH_SHORT).show()


            val db = MainDB.getDB(requireContext())

            CoroutineScope(Dispatchers.IO).launch {
//                db.userDao().insertUser(
//                    Users(
//                        Name = "Test",
//                        Email = "test@gmail.com",
//                        Password = "123",
//                        Balance = 100f
//                    )
//                )

                db.userDao().getAllUsers().collect { list ->
                    list.forEach {
                        Log.d("ROOM_TEST", "User: ${it.Name}, Email: ${it.Email}, Balance: ${it.Balance}")
                    }
                }

//                db.userDao().deleteAllUsers()
            }
        }



    }
}

