package com.example.androiddev2025.dadibranch.fragments

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.androiddev2025.MainActivity
import com.example.androiddev2025.R
import com.example.androiddev2025.Session
import com.example.androiddev2025.Database.MainDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.fragment.findNavController

class SettingsFragment : Fragment(R.layout.user_page_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userNameTv = view.findViewById<TextView>(R.id.UserName)
        val userEmailTv = view.findViewById<TextView>(R.id.UserEmail)
        val userPasswordTv = view.findViewById<TextView>(R.id.UserPassword)
        val userBalanceTv = view.findViewById<TextView>(R.id.UserBalance)

        var passwordCache = ""

        val emailToEdit = arguments?.getString("user_email") ?: Session.email
        if (emailToEdit == null) return

        // загрузка данных
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = MainDB.getDB(requireContext()).userDao()
            val user = dao.getUserByEmail(emailToEdit)

            withContext(Dispatchers.Main) {
                if (user == null) {
                    Toast.makeText(requireContext(), "Пользователь не найден", Toast.LENGTH_SHORT).show()
                    return@withContext
                }
                userNameTv.text = user.name
                userEmailTv.text = user.email
                userPasswordTv.text = "••••••••"
                userBalanceTv.text = "Balance: ${user.balance}"
                passwordCache = user.password
            }
        }

        // блоки редактирования
        val nameView = view.findViewById<LinearLayout>(R.id.UserNameView)
        val nameEditBlock = view.findViewById<LinearLayout>(R.id.UserNameEditBlock)
        val nameChangeBTN = view.findViewById<Button>(R.id.NameChangeButton)
        val userNameEdit = view.findViewById<EditText>(R.id.UserNameEdit)
        val userNameEditBTN = view.findViewById<Button>(R.id.NameEditButton)

        val emailView = view.findViewById<LinearLayout>(R.id.UserEmailView)
        val emailEditBlock = view.findViewById<LinearLayout>(R.id.UserEmailEditBlock)
        val emailChangeBTN = view.findViewById<Button>(R.id.EmailChangeButton)
        val emailEdit = view.findViewById<EditText>(R.id.UserEmailEdit)
        val emailEditBTN = view.findViewById<Button>(R.id.EmailEditButton)

        val passwordView = view.findViewById<LinearLayout>(R.id.UserPasswordView)
        val passwordEditBlock = view.findViewById<LinearLayout>(R.id.UserPasswordEditBlock)
        val passwordChangeBTN = view.findViewById<Button>(R.id.PasswordChangeButton)
        val passwordEdit = view.findViewById<EditText>(R.id.UserPasswordEdit)
        val passwordEditBTN = view.findViewById<Button>(R.id.PasswordEditButton)

        nameChangeBTN.setOnClickListener {
            nameView.visibility = View.GONE
            nameEditBlock.visibility = View.VISIBLE
            userNameEdit.setText(userNameTv.text.toString())
        }

        emailChangeBTN.setOnClickListener {
            emailView.visibility = View.GONE
            emailEditBlock.visibility = View.VISIBLE
            emailEdit.setText(userEmailTv.text.toString())
        }

        passwordChangeBTN.setOnClickListener {
            passwordView.visibility = View.GONE
            passwordEditBlock.visibility = View.VISIBLE
            passwordEdit.setText(passwordCache)
        }

        val isEditingSelf = (emailToEdit == Session.email)

        userNameEditBTN.setOnClickListener {
            val newName = userNameEdit.text.toString().trim()
            if (newName.isEmpty()) return@setOnClickListener

            lifecycleScope.launch(Dispatchers.IO) {
                val dao = MainDB.getDB(requireContext()).userDao()
                val user = dao.getUserByEmail(emailToEdit) ?: return@launch
                dao.updateUser(user.copy(name = newName))

                withContext(Dispatchers.Main) {
                    userNameTv.text = newName
                    nameEditBlock.visibility = View.GONE
                    nameView.visibility = View.VISIBLE

                    if (isEditingSelf) {
                        Session.name = newName
                        (requireActivity() as? MainActivity)?.updateToolbar()
                    }
                    Toast.makeText(requireContext(), "Имя обновлено", Toast.LENGTH_SHORT).show()
                }
            }
        }

        emailEditBTN.setOnClickListener {
            val newEmail = emailEdit.text.toString().trim()
            if (newEmail.isEmpty()) return@setOnClickListener

            lifecycleScope.launch(Dispatchers.IO) {
                val dao = MainDB.getDB(requireContext()).userDao()
                val user = dao.getUserByEmail(emailToEdit) ?: return@launch
                dao.updateUser(user.copy(email = newEmail))

                withContext(Dispatchers.Main) {
                    userEmailTv.text = newEmail
                    emailEditBlock.visibility = View.GONE
                    emailView.visibility = View.VISIBLE

                    if (isEditingSelf) {
                        Session.email = newEmail
                    }
                    Toast.makeText(requireContext(), "Почта обновлена", Toast.LENGTH_SHORT).show()
                }
            }
        }

        passwordEditBTN.setOnClickListener {
            val newPassword = passwordEdit.text.toString().trim()
            if (newPassword.isEmpty()) return@setOnClickListener

            lifecycleScope.launch(Dispatchers.IO) {
                val dao = MainDB.getDB(requireContext()).userDao()
                val user = dao.getUserByEmail(emailToEdit) ?: return@launch
                dao.updateUser(user.copy(password = newPassword))

                withContext(Dispatchers.Main) {
                    passwordCache = newPassword
                    userPasswordTv.text = "••••••••"
                    passwordEditBlock.visibility = View.GONE
                    passwordView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Пароль обновлён", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val addBalanceBtn = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.AddBalanceButton)
        val logoutBtn = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.LogoutButton)

        addBalanceBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val dao = MainDB.getDB(requireContext()).userDao()
                val user = dao.getUserByEmail(emailToEdit) ?: return@launch
                val newBalance = user.balance + 100
                dao.updateUser(user.copy(balance = newBalance))

                withContext(Dispatchers.Main) {
                    view.findViewById<TextView>(R.id.UserBalance).text = "Balance: $newBalance"
                    Toast.makeText(requireContext(), "+100 added", Toast.LENGTH_SHORT).show()
                }
            }
        }

        logoutBtn.setOnClickListener {
            Session.logout()
            (requireActivity() as MainActivity).switchToAuthGraph()
        }

    }
}
