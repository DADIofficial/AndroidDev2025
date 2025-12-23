package com.example.androiddev2025.dadibranch.fragments.GameList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddev2025.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

class LobbyAdapter(
    private val items: List<LobbyData>,
    private val onJoinClick: (LobbyData) -> Unit
) : RecyclerView.Adapter<LobbyAdapter.LobbyViewHolder>() {

    inner class LobbyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameName: TextView = view.findViewById(R.id.game_name)
        val playersChip: Chip = view.findViewById(R.id.players_chip)
        val typeValue: TextView = view.findViewById(R.id.type_value)
        val balanceValue: TextView = view.findViewById(R.id.balance_value)
        val hostValue: TextView = view.findViewById(R.id.host_value)
        val joinButton: MaterialButton = view.findViewById(R.id.join_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.interaction_multiple_game_block, parent, false)
        return LobbyViewHolder(view)
    }

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        val lobby = items[position]

        holder.gameName.text = lobby.name
        holder.playersChip.text = "${lobby.currentPlayers}/${lobby.maxPlayers}"
        holder.typeValue.text = lobby.type
        holder.balanceValue.text = lobby.balanceRange
        holder.hostValue.text = lobby.host

        holder.joinButton.setOnClickListener {
            onJoinClick(lobby)
        }
    }

    override fun getItemCount(): Int = items.size
}
