package com.example.androiddev2025

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddev2025.Database.MainDB
import com.example.androiddev2025.Database.Users
import com.example.androiddev2025.RecyclerView.Adapter
import com.example.androiddev2025.RecyclerView.DataRV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminPageFrag: Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.admin_page_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.adminrecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            MainDB.getDB(requireContext()).userDao().getAllUsers().collect { users ->
                adapter = Adapter(users) { selectedUser ->
                    openUserPage(selectedUser)
                }
                recyclerView.adapter = adapter
            }
        }
    }


    private fun openUserPage(user: Users) {
        val bundle = Bundle().apply { putString("user_email", user.email) }

        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.UserPageFragment, bundle)
    }

}