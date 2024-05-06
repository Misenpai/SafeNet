package com.example.safenet.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.safenet.R
import com.google.firebase.auth.FirebaseAuth

class fragment_signup : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var signupEmail:EditText
    private lateinit var signupPassword:EditText
    private lateinit var signupButton:Button
    private lateinit var signupPasswordConfirm:EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        signupEmail = view.findViewById(R.id.email_field_signup)
        signupPassword = view.findViewById(R.id.password_field_signup)
        signupButton = view.findViewById(R.id.signup_btn)
        signupPasswordConfirm = view.findViewById(R.id.confirm_password_field_signup)

        signupButton.setOnClickListener {
            val email = signupEmail.text.toString().trim()
            val password = signupPassword.text.toString().trim()
            val confirmPassword = signupPasswordConfirm.text.toString().trim()

            when {
                email.isEmpty() -> signupEmail.error = "Email cannot be empty"
                password.isEmpty() -> signupPassword.error = "Password cannot be empty"
                confirmPassword.isEmpty() -> signupPasswordConfirm.error = "Confirm password cannot be empty"
                password != confirmPassword -> {
                    signupPasswordConfirm.error = "Passwords do not match"
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "SignUp Successful", Toast.LENGTH_SHORT).show()
                            } else {
                                val errorMessage = task.exception?.message ?: "Unknown error"
                                Toast.makeText(requireContext(), "SignUp Failed: $errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
}