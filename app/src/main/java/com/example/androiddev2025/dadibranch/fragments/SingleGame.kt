package com.example.androiddev2025.dadibranch.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.androiddev2025.Database.MainDB
import com.example.androiddev2025.R
import com.example.androiddev2025.Session
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random


class SingleGame : Fragment(R.layout.single_game_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val balanceText = view.findViewById<TextView>(R.id.balanceText)
        val betInput = view.findViewById<EditText>(R.id.betInput)
        val playButton = view.findViewById<MaterialButton>(R.id.playButton)
        val resultText = view.findViewById<TextView>(R.id.resultText)

        val userId = Session.userId ?: return
        val userDao = MainDB.getDB(requireContext()).userDao()

        fun updateBalanceUI() {
            balanceText.text = "Balance: ${Session.balance}"
        }

        updateBalanceUI()

        playButton.setOnClickListener {

            val bet = betInput.text.toString().toIntOrNull()

            if (bet == null || bet <= 0) {
                Toast.makeText(requireContext(), "Invalid bet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (bet > Session.balance) {
                Toast.makeText(requireContext(), "Not enough balance", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val win = Random.nextBoolean()

            lifecycleScope.launch {

                val newBalance = if (win) {
                    Session.balance + bet*2f
                } else {
                    Session.balance - bet
                }

                Session.balance = newBalance

                withContext(Dispatchers.IO) {
                    val user = userDao.getUserById(userId) ?: return@withContext
                    userDao.updateUser(
                        user.copy(balance = newBalance)
                    )
                }

                updateBalanceUI()

                resultText.text =
                    if (win) "You won! +$bet*2f"
                    else "You lost! -$bet"
            }
        }
    }
}
