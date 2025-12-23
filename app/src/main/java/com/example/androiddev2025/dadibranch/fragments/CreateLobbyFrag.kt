package com.example.androiddev2025.dadibranch.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androiddev2025.R
import com.example.androiddev2025.dadibranch.fragments.GameDataBase.GameDB
import com.example.androiddev2025.dadibranch.fragments.GameDataBase.Lobby
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import com.example.androiddev2025.Session


class CreateLobbyFrag : Fragment(R.layout.create_lobby_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = GameDB.getDB(requireContext())

        val name = view.findViewById<EditText>(R.id.lobby_name)
        val max = view.findViewById<EditText>(R.id.lobby_max)
        val create = view.findViewById<MaterialButton>(R.id.create_button)
        val balanceMin = view.findViewById<EditText>(R.id.lobbyBalanceMin)
        val balanceMax = view.findViewById<EditText>(R.id.lobbyBalanceMax)


        create.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {

                val hostId = Session.userId ?: error("User must be logged in")


                val min = balanceMin.text.toString().toIntOrNull() ?: 0
                val maxBal = balanceMax.text.toString().toIntOrNull() ?: 0

                db.lobbyDao().insert(
                    Lobby(
                        name = name.text.toString(),
                        hostId = hostId,
                        maxPlayers = max.text.toString().toInt(),
                        playersIds = hostId.toString(),
                        balanceMin = min,
                        balanceMax = maxBal
                    )
                )


                withContext(Dispatchers.Main) {
                    findNavController().popBackStack()
                }
            }
        }


    }
}
