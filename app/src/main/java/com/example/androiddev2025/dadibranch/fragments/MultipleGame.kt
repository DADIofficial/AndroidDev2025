package com.example.androiddev2025.dadibranch.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddev2025.Database.MainDB
import com.example.androiddev2025.R
import com.example.androiddev2025.Session
import com.example.androiddev2025.dadibranch.fragments.GameDataBase.GameDB
import com.example.androiddev2025.dadibranch.fragments.GameList.LobbyAdapter
import com.example.androiddev2025.dadibranch.fragments.GameList.LobbyData
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MultipleGame : Fragment(R.layout.fragment_multiple_game) {

    private lateinit var db: GameDB
    private lateinit var recycler: RecyclerView

    private var headerNameTv: TextView? = null
    private var headerEmailTv: TextView? = null
    private var headerBalanceTv: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = GameDB.getDB(requireContext())

        headerNameTv = view.findViewById(R.id.username)
        headerEmailTv = view.findViewById(R.id.username_subtitle)
        headerBalanceTv = view.findViewById(R.id.Balance_number)

        recycler = view.findViewById(R.id.recyclerview)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<MaterialButton>(R.id.create_lobby_button).setOnClickListener {
            findNavController().navigate(R.id.createLobbyFragment)
        }

        refreshHeader()
        loadLobbies()
    }

    override fun onResume() {
        super.onResume()
        refreshHeader()
    }

    private fun refreshHeader() {
        val email = Session.email ?: return

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val user = MainDB.getDB(requireContext()).userDao().getUserByEmail(email)

            withContext(Dispatchers.Main) {
                val nameTv = headerNameTv ?: return@withContext
                val emailTv = headerEmailTv ?: return@withContext
                val balanceTv = headerBalanceTv ?: return@withContext

                if (user == null) {
                    nameTv.text = "Player"
                    emailTv.text = email
                    balanceTv.text = "0"
                } else {
                    nameTv.text = user.name
                    emailTv.text = user.email

                     val b = user.balance
                    balanceTv.text = if (b % 1f == 0f) b.toInt().toString() else b.toString()
                }
            }
        }
    }

    private fun loadLobbies() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val usersDao = MainDB.getDB(requireContext()).userDao()

            val lobbies = withContext(Dispatchers.IO) {
                db.lobbyDao().getAll()
            }

            val data = lobbies.map { lobby ->
                val hostName = withContext(Dispatchers.IO) {
                    usersDao.getUserById(lobby.hostId)?.name ?: "Unknown"
                }

                LobbyData(
                    id = lobby.id,
                    name = lobby.name,
                    currentPlayers = lobby.playersIds.split(",").filter { it.isNotBlank() }.size,
                    maxPlayers = lobby.maxPlayers,
                    host = hostName,
                    type = "Multiple Choice",
                    balanceRange = "${lobby.balanceMin} – ${lobby.balanceMax}"
                )
            }

            recycler.adapter = LobbyAdapter(data) {
                val bundle = Bundle().apply { putInt("lobbyId", it.id) }
                findNavController().navigate(R.id.lobbyDetailsFragment, bundle)
            }
        }
    }
}
