package com.example.androiddev2025

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.androiddev2025.ApiServ.Retrofit
import com.example.androiddev2025.Database.MainDB
import com.example.androiddev2025.Database.Users
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first


class LogInPageFrag : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_page_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registr = view.findViewById<TextView>(R.id.RegistrL)
        val loginbutton = view.findViewById<Button>(R.id.LoginButton)

        val email = view.findViewById<EditText>(R.id.LoginEmail)
        val password = view.findViewById<EditText>(R.id.LoginPassword)

        registr.setOnClickListener {
            findNavController().navigate(R.id.registrFragment)
        }


        loginbutton.setOnClickListener {
            val emailT = email.text.toString().trim()
            val passwordT = password.text.toString().trim()

            if (!isValidEmail(emailT) || !isValidPassword(passwordT)) {
                Toast.makeText(requireContext(), "Заполните Email и Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(emailT, passwordT)
        }


    }

    private fun login(email: String, password: String) {
        val db = MainDB.getDB(requireContext()).userDao()
        val db1 = MainDB.getDB(requireContext())

        CoroutineScope(Dispatchers.IO).launch {

            if (checkInet()) {
                try {
                    val apiUsers = Retrofit.api.getUsers(5).users


                    val dbList = apiUsers.map { user ->
                        Users(
                            id = null,
                            name = "${user.firstName} ${user.lastName}",
                            email = user.email,
                            password = user.password,
                            balance = 0f,
                            admin = false
                        )
                    }

                    db.deleteAllUsers()
                    db.insertAll(dbList)

                    val apiUser = apiUsers.find { it.email == email && it.password == password }
                    if (apiUser != null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "вход  API", Toast.LENGTH_SHORT).show()
                        }
                        val list = db1.userDao().getAllUsers().first()
                        list.forEach {
                            Log.d("ROOM_TEST", "User: ${it.name}, Email: ${it.email}, Password: ${it.password}, Balance: ${it.balance}, Admin: ${it.admin}")
                        }
                    }

                } catch (e: Exception) {
                    Log.e("error", e.toString())

                }
            }

            val localL = db.getAllUsersOnce()
            val localU = localL.find { it.email == email && it.password == password }

            if (localU != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "проверка Room", Toast.LENGTH_SHORT).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Неверный Email или Password", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun isValidEmail(text: String) = text.isNotEmpty()
    private fun isValidPassword(text: String) = text.isNotEmpty()


    private fun checkInet(): Boolean {
        return true
    }
}
