package com.example.androiddev2025.dadibranch.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.androiddev2025.Database.MainDB
import com.example.androiddev2025.R
import com.example.androiddev2025.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class SingleGame : Fragment(R.layout.fragment_single_game) {

    private var refreshJob: Job? = null

    private lateinit var userNameTv: TextView
    private lateinit var userSubtitleTv: TextView
    private lateinit var balanceNumberTv: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userNameTv = view.findViewById(R.id.username)
        userSubtitleTv = view.findViewById(R.id.username_subtitle)
        balanceNumberTv = view.findViewById(R.id.Balance_number)

        refreshHeaderFromDb()
    }

    override fun onResume() {
        super.onResume()
        refreshHeaderFromDb()
    }

    private fun refreshHeaderFromDb() {
        val email = Session.email
        if (email.isNullOrBlank()) {
            userNameTv.text = "Guest"
            userSubtitleTv.text = ""
            balanceNumberTv.text = "0"
            return
        }

        refreshJob?.cancel()
        refreshJob = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val dao = MainDB.getDB(requireContext()).userDao()
            val user = dao.getUserByEmail(email)

            withContext(Dispatchers.Main) {
                if (!isAdded) return@withContext

                if (user == null) {
                    Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show()
                    return@withContext
                }

                userNameTv.text = user.name
                userSubtitleTv.text = user.email
                balanceNumberTv.text = formatBalance(user.balance)

                Session.name = user.name
                Session.balance = user.balance
                Session.admin = user.admin
            }
        }
    }

    private fun formatBalance(balance: Float): String {
        val rounded = balance.roundToInt()
        return if (kotlin.math.abs(balance - rounded) < 0.0001f) {
            rounded.toString()
        } else {
            balance.toString()
        }
    }
}
