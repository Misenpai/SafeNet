package com.example.safenet.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.safenet.MainActivity
import com.example.safenet.R
import com.google.firebase.auth.FirebaseAuth

class fragment_login : Fragment() {

    private lateinit var loginEmail:EditText
    private lateinit var loginPassword:EditText
    private lateinit var loginButtion:Button
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        loginEmail = view.findViewById(R.id.email_field_login)
        loginButtion = view.findViewById(R.id.login_btn)
        loginPassword = view.findViewById(R.id.password_field_login)

        loginButtion.setOnClickListener{
            val email = loginEmail.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            when {
                email.isEmpty() -> loginEmail.error = "Email cannot be empty"
                password.isEmpty() -> loginPassword.error = "Password cannot be empty"
                else -> {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                                // Navigate to the main fragment or activity after successful login
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                                // Optionally, you can finish the current activity or fragment after navigating
                                activity?.finish()

                            } else {
                                val errorMessage = task.exception?.message ?: "Unknown error"
                                Toast.makeText(requireContext(), "Login Failed: $errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

        }


    }
}