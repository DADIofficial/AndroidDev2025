package com.example.androiddev2025.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddev2025.R
import com.example.androiddev2025.Database.Users


class Adapter(
    private val users: List<Users>,
    private val onCheckClick: (Users) -> Unit
) : RecyclerView.Adapter<Adapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userId = itemView.findViewById<TextView>(R.id.AP_UserID)
        val userName = itemView.findViewById<TextView>(R.id.AP_UserName)
        val userEmail = itemView.findViewById<TextView>(R.id.AP_UserEmail)
        val checkButton = itemView.findViewById<Button>(R.id.UserCheckButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.userId.text = user.id.toString()
        holder.userName.text = user.name
        holder.userEmail.text = user.email

        holder.checkButton.setOnClickListener {
            onCheckClick(user)
        }
    }

    override fun getItemCount(): Int = users.size
}
