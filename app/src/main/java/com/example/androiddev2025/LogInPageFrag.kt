package com.example.androiddev2025

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class LogInPageFrag : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_page_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registr = view.findViewById<TextView>(R.id.RegistrL)

        registr.setOnClickListener {
            findNavController().navigate(R.id.registrFragment)
        }

//        view.findViewById<EditText>(R.id.LoginEmail).doOnTextChanged { text, start, before, count ->
//            validate(text)
//        }

    }

//    private fun validate(text: String) {
//        if (text.isEmpty()) Toast.makeText(, "", Toast.LENGTH_SHORT).show()
//        if (text.length < 6) Toast.makeText(, "", Toast.LENGTH_SHORT).show()
//    }
}
