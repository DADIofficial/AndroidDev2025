package com.example.androiddev2025

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.androiddev2025.Database.MainDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainPageFrag: Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_page_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val balanceText = view.findViewById<TextView>(R.id.textView)
        val plusBtn = view.findViewById<Button>(R.id.PlusBalance)
        val minusBtn = view.findViewById<Button>(R.id.MinusBalance)

        balanceText.text = "Balance: ${Session.balance}"

        plusBtn.setOnClickListener {
            updateBalance(10f) { newBalance ->
                balanceText.text = "Balance: $newBalance"
            }
        }

        minusBtn.setOnClickListener {
            updateBalance(-10f) { newBalance ->
                balanceText.text = "Balance: $newBalance"
            }
        }
    }

    private fun updateBalance(amount: Float, callback: (Float) -> Unit) {
        val email = Session.email ?: return
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val dao = MainDB.getDB(requireContext()).userDao()
            val user = dao.getUserByEmail(email) ?: return@launch

            var newBalance = user.balance + amount
            if (newBalance < 0) newBalance = 0f

            val updatedUser = user.copy(balance = newBalance)
            dao.updateUser(updatedUser)

            Session.balance = newBalance
            withContext(Dispatchers.Main) {
                callback(newBalance)
            }
        }
    }

}