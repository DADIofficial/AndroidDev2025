package com.example.androiddev2025.dadibranch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddev2025.Database.MainDB
import com.example.androiddev2025.Database.Users
import com.example.androiddev2025.MainActivity
import com.example.androiddev2025.R
import com.example.androiddev2025.RecyclerView.Adapter
import com.example.androiddev2025.Session
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    companion object {
        private const val ARG_USER_EMAIL = "user_email"
    }

    /** Если админ и НЕ передали user_email — показываем админ-панель */
    private fun isAdminListMode(): Boolean {
        val emailArg = arguments?.getString(ARG_USER_EMAIL)
        return Session.admin && emailArg.isNullOrEmpty()
    }

    /** Для user layout: какой email редактируем */
    private fun resolveEmailToEdit(): String? {
        return arguments?.getString(ARG_USER_EMAIL) ?: Session.email
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layoutId = if (isAdminListMode()) {
            R.layout.admin_page_layout
        } else {
            R.layout.user_page_layout
        }
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAdminListMode()) {
            setupAdminPage(view)
        } else {
            val emailToEdit = resolveEmailToEdit() ?: return
            setupUserPage(view, emailToEdit)
        }
    }

    // ------------------------
    // ADMIN PAGE (list of users)
    // ------------------------
    private fun setupAdminPage(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.adminrecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            MainDB.getDB(requireContext()).userDao().getAllUsers().collect { users ->
                val adapter = Adapter(users) { selectedUser ->
                    openUserEditor(selectedUser)
                }
                recyclerView.adapter = adapter
            }
        }
    }

    private fun openUserEditor(user: Users) {
        // Открываем тот же SettingsFragment, но уже в режиме user_page_layout
        val bundle = Bundle().apply { putString(ARG_USER_EMAIL, user.email) }

        // launchSingleTop, чтобы не плодить копии
        val nav = findNavController()
        nav.navigate(R.id.settingsFragment, bundle)
    }

    // ------------------------
    // USER PAGE (edit user data)
    // ------------------------
    private fun setupUserPage(view: View, emailToEditInitial: String) {

        // UI refs (user_page_layout)
        val userNameTv = view.findViewById<TextView>(R.id.UserName)
        val userEmailTv = view.findViewById<TextView>(R.id.UserEmail)
        val userPasswordTv = view.findViewById<TextView>(R.id.UserPassword)
        val userBalanceTv = view.findViewById<TextView>(R.id.UserBalance)

        var passwordCache = ""

        val userId = Session.userId ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val dao = MainDB.getDB(requireContext()).userDao()
            val user = dao.getUserById(userId) ?: return@launch

            withContext(Dispatchers.Main) {
                userNameTv.text = user.name
                userEmailTv.text = user.email
                userPasswordTv.text = "••••••••"
                userBalanceTv.text = "Balance: ${user.balance}"
                passwordCache = user.password
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

        val addBalanceBtn = view.findViewById<MaterialButton>(R.id.AddBalanceButton)
        val logoutBtn = view.findViewById<MaterialButton>(R.id.LogoutButton)

        var passwordCache = ""
        var currentEmail = emailToEditInitial
        val isEditingSelf = (currentEmail == Session.email)

        if (!isEditingSelf) {
             logoutBtn.visibility = View.GONE
        }


        lifecycleScope.launch(Dispatchers.IO) {
            val dao = MainDB.getDB(requireContext()).userDao()
            val user = dao.getUserByEmail(currentEmail)

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

        userNameEditBTN.setOnClickListener {
            val newName = userNameEdit.text.toString().trim()
            if (newName.isEmpty()) return@setOnClickListener

            lifecycleScope.launch(Dispatchers.IO) {
                val dao = MainDB.getDB(requireContext()).userDao()
                val user = dao.getUserById(userId) ?: return@launch
                dao.updateUser(user.copy(name = newName))

                withContext(Dispatchers.Main) {
                    userNameTv.text = newName
                    nameEditBlock.visibility = View.GONE
                    nameView.visibility = View.VISIBLE

                    Session.name = newName
                    (requireActivity() as? MainActivity)?.updateToolbar()

                    Toast.makeText(requireContext(), "Имя обновлено", Toast.LENGTH_SHORT).show()
                }
            }
        }


        emailEditBTN.setOnClickListener {
            val newEmail = emailEdit.text.toString().trim()
            if (newEmail.isEmpty()) return@setOnClickListener

            lifecycleScope.launch(Dispatchers.IO) {
                val dao = MainDB.getDB(requireContext()).userDao()
                val user = dao.getUserById(userId) ?: return@launch
                dao.updateUser(user.copy(email = newEmail))

                withContext(Dispatchers.Main) {
                    userEmailTv.text = newEmail
                    emailEditBlock.visibility = View.GONE
                    emailView.visibility = View.VISIBLE

                    Session.email = newEmail
                    Toast.makeText(requireContext(), "Почта обновлена", Toast.LENGTH_SHORT).show()
                }
            }
        }

        passwordEditBTN.setOnClickListener {
            val newPassword = passwordEdit.text.toString().trim()
            if (newPassword.isEmpty()) return@setOnClickListener

            lifecycleScope.launch(Dispatchers.IO) {
                val dao = MainDB.getDB(requireContext()).userDao()
                val user = dao.getUserById(userId) ?: return@launch
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


        addBalanceBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val dao = MainDB.getDB(requireContext()).userDao()
                val user = dao.getUserById(userId) ?: return@launch
                val newBalance = user.balance + 10
                dao.updateUser(user.copy(balance = newBalance))

                withContext(Dispatchers.Main) {
                    Session.balance = newBalance
                    userBalanceTv.text = "Balance: $newBalance"
                    Toast.makeText(requireContext(), "+10 added", Toast.LENGTH_SHORT).show()
                }
            }
        }

        logoutBtn.setOnClickListener {
            Session.logout()
            (requireActivity() as MainActivity).switchToAuthGraph()
        }
    }
}
