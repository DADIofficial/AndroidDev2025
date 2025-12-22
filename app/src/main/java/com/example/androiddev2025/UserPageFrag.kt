package com.example.androiddev2025

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.androiddev2025.Database.MainDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserPageFrag : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_page_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userNameTv = view.findViewById<TextView>(R.id.UserName)
        val userEmailTv = view.findViewById<TextView>(R.id.UserEmail)
        val userPasswordTv = view.findViewById<TextView>(R.id.UserPassword)
        val userBalanceTv = view.findViewById<TextView>(R.id.UserBalance)

        var password: String = ""

        val email = arguments?.getString("user_email") ?: Session.email


        if (email == null) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val userDao = MainDB.getDB(requireContext()).userDao()
            val user = userDao.getUserByEmail(email)

            withContext(Dispatchers.Main) {
                if (user != null) {
                    userNameTv.text = user.name
                    userEmailTv.text = user.email
                    userPasswordTv.text = "••••••••"
                    userBalanceTv.text = "Balance: ${user.balance}"

                    password = user.password
                }
                else { Toast.makeText(requireContext(),"Пользователь не найден",Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }



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
            nameView.visibility = View.INVISIBLE
            nameEditBlock.visibility = View.VISIBLE
            userNameEdit.setText(userNameTv.text.toString())
        }

        emailChangeBTN.setOnClickListener {
            emailView.visibility = View.INVISIBLE
            emailEditBlock.visibility = View.VISIBLE
            emailEdit.setText(userEmailTv.text.toString())
        }

        passwordChangeBTN.setOnClickListener {
            passwordView.visibility = View.INVISIBLE
            passwordEditBlock.visibility = View.VISIBLE
            passwordEdit.setText(password)
        }


        userNameEditBTN.setOnClickListener {
            val newName = userNameEdit.text.toString().trim()
            if (newName.isEmpty()) return@setOnClickListener

            CoroutineScope(Dispatchers.IO).launch {
                val dao = MainDB.getDB(requireContext()).userDao()
                val user = dao.getUserByEmail(email) ?: return@launch

                val updatedUser = user.copy(name = newName)
                dao.updateUser(updatedUser)

                Session.name = newName

                withContext(Dispatchers.Main) {
                    userNameTv.text = newName
                    nameEditBlock.visibility = View.GONE
                    nameView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Имя обновлено", Toast.LENGTH_SHORT).show()
                }
            }
        }

        emailEditBTN.setOnClickListener {
            val newEmail = emailEdit.text.toString().trim()
            if (newEmail.isEmpty()) return@setOnClickListener

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val dao = MainDB.getDB(requireContext()).userDao()
                val user = dao.getUserByEmail(email) ?: return@launch

                val updatedUser = user.copy(email = newEmail)
                dao.updateUser(updatedUser)

                Session.email = newEmail

                withContext(Dispatchers.Main) {
                    userEmailTv.text = newEmail
                    emailEditBlock.visibility = View.GONE
                    emailView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Почта обновлена", Toast.LENGTH_SHORT).show()
                }
            }
        }

        passwordEditBTN.setOnClickListener {
            val newPassword = passwordEdit.text.toString().trim()
            if (newPassword.isEmpty()) return@setOnClickListener

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val dao = MainDB.getDB(requireContext()).userDao()
                val user = dao.getUserByEmail(email) ?: return@launch

                val updatedUser = user.copy(password = newPassword)
                dao.updateUser(updatedUser)

                withContext(Dispatchers.Main) {
                    userPasswordTv.text = "••••••••"
                    passwordEditBlock.visibility = View.GONE
                    passwordView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Пароль обновлён", Toast.LENGTH_SHORT).show()
                }
            }
        }





    }
}
