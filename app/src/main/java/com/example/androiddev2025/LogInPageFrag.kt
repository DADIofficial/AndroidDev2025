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

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.navigation.findNavController


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
        val dao = MainDB.getDB(requireContext()).userDao()

        CoroutineScope(Dispatchers.IO).launch {

            var roomUser: Users? = null

            if (checkInet()) {
                try {
                    val apiUsers = Retrofit.api.getUsers(5).users
                    val apiUser = apiUsers.find {
                        it.email == email && it.password == password
                    }

                    if (apiUser != null) {

                        roomUser = dao.getUserByEmail(email)

                        if (roomUser == null) {
                            roomUser = Users(
                                id = null,
                                name = "${apiUser.firstName} ${apiUser.lastName}",
                                email = apiUser.email,
                                password = apiUser.password,
                                balance = 0f,
                                admin = false
                            )
                            dao.insertUser(roomUser)
                            roomUser = dao.getUserByEmail(email)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("LOGIN", "API error", e)
                }
            }

            if (roomUser == null) {
                roomUser = dao.getUserByEmail(email)
                if (roomUser?.password != password) {
                    roomUser = null
                }
            }

            withContext(Dispatchers.Main) {
                if (roomUser != null) {
                    Session.login(
                        email = roomUser.email,
                        name = roomUser.name,
                        admin = roomUser.admin,
                        balance = roomUser.balance
                    )

                    (requireActivity() as MainActivity).updateToolbar()
                    findNavController().navigate(R.id.mainFragment)

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Неверный Email или Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }



    private fun isValidEmail(text: String) = text.isNotEmpty()
    private fun isValidPassword(text: String) = text.isNotEmpty()


    private fun checkInet(): Boolean {
        val cm = requireContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    private suspend fun syncUsersFromApi() {
        val db = MainDB.getDB(requireContext()).userDao()
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
    }


}
