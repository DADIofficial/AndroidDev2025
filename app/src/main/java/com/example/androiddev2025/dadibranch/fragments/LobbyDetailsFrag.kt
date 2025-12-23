package com.example.androiddev2025.dadibranch.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.androiddev2025.Database.MainDB
import com.example.androiddev2025.R
import com.example.androiddev2025.Session
import com.example.androiddev2025.dadibranch.fragments.GameDataBase.GameDB
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LobbyDetailsFrag : Fragment(R.layout.lobby_details_layout) {

    private lateinit var gameDb: GameDB
    private var lobbyId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameDb = GameDB.getDB(requireContext())
        lobbyId = requireArguments().getInt("lobbyId")

        val joinButton = view.findViewById<MaterialButton>(R.id.JointoGame)
        val quitButton = view.findViewById<MaterialButton>(R.id.QuitGame)
        val returnButton = view.findViewById<MaterialButton>(R.id.ReturnToLobby)
        val playersText = view.findViewById<TextView>(R.id.players)
        val lobbyName = view.findViewById<TextView>(R.id.lobby_name)

        val userDao = MainDB.getDB(requireContext()).userDao()
        val currentUserId = Session.userId ?: return

        fun renderLobby(lobbyPlayersIds: List<Int>, lobbyHostId: Int, maxPlayers: Int) {

            lifecycleScope.launch {
                val names = withContext(Dispatchers.IO) {
                    lobbyPlayersIds.mapNotNull { id ->
                        userDao.getUserById(id)?.name
                    }
                }
                playersText.text = names.joinToString("\n")
            }

            when {
                currentUserId == lobbyHostId -> {
                    joinButton.visibility = View.GONE
                    quitButton.visibility = View.GONE
                }

                lobbyPlayersIds.contains(currentUserId) -> {
                    joinButton.visibility = View.GONE
                    quitButton.visibility = View.VISIBLE
                }

                lobbyPlayersIds.size >= maxPlayers -> {
                    joinButton.visibility = View.GONE
                    quitButton.visibility = View.GONE
                }

                else -> {
                    joinButton.visibility = View.VISIBLE
                    quitButton.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launch {

            val lobby = withContext(Dispatchers.IO) {
                gameDb.lobbyDao().getById(lobbyId)
            }

            lobbyName.text = lobby.name

            val playersIds = lobby.playersIds
                .split(",")
                .filter { it.isNotBlank() }
                .map { it.toInt() }

            renderLobby(playersIds, lobby.hostId, lobby.maxPlayers)
        }

        joinButton.setOnClickListener {

            lifecycleScope.launch {

                val freshLobby = withContext(Dispatchers.IO) {
                    gameDb.lobbyDao().getById(lobbyId)
                }

                val freshPlayersIds = freshLobby.playersIds
                    .split(",")
                    .filter { it.isNotBlank() }
                    .map { it.toInt() }

                if (freshPlayersIds.contains(currentUserId)) return@launch

                val updatedPlayers =
                    (freshPlayersIds + currentUserId)
                        .distinct()
                        .joinToString(",")

                withContext(Dispatchers.IO) {
                    gameDb.lobbyDao().updatePlayers(
                        id = lobbyId,
                        players = updatedPlayers
                    )
                }

                val updatedIds = updatedPlayers
                    .split(",")
                    .filter { it.isNotBlank() }
                    .map { it.toInt() }

                renderLobby(updatedIds, freshLobby.hostId, freshLobby.maxPlayers)
            }
        }

        quitButton.setOnClickListener {

            lifecycleScope.launch {

                val freshLobby = withContext(Dispatchers.IO) {
                    gameDb.lobbyDao().getById(lobbyId)
                }

                val freshPlayersIds = freshLobby.playersIds
                    .split(",")
                    .filter { it.isNotBlank() }
                    .map { it.toInt() }

                val updatedPlayers =
                    freshPlayersIds
                        .filter { it != currentUserId }
                        .joinToString(",")

                withContext(Dispatchers.IO) {
                    gameDb.lobbyDao().updatePlayers(
                        id = lobbyId,
                        players = updatedPlayers
                    )
                }

                val updatedIds = updatedPlayers
                    .split(",")
                    .filter { it.isNotBlank() }
                    .map { it.toInt() }

                renderLobby(updatedIds, freshLobby.hostId, freshLobby.maxPlayers)
            }
        }

        returnButton.setOnClickListener {
            findNavController().navigate(R.id.multipleGameFragment)
        }
    }
}
