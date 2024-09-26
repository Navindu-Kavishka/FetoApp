@file:Suppress("DEPRECATION")

package com.example.fetoapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.fetoapp.R
import com.example.fetoapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var pd: ProgressDialog
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        pd = ProgressDialog(this)

        binding.signUpTexttoSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        pd = ProgressDialog(this)

        binding.signUpButton.setOnClickListener {
            val email = binding.signUpnetemail.text.toString()
            val password = binding.signUpetpassword.text.toString()
            val name = binding.signUpName.text.toString()

            when {
                email.isEmpty() -> Toast.makeText(this, "Enter Your Email", Toast.LENGTH_SHORT).show()
                password.isEmpty() -> Toast.makeText(this, "Enter Your Password", Toast.LENGTH_SHORT).show()
                name.isEmpty() -> Toast.makeText(this, "Enter Your Name", Toast.LENGTH_SHORT).show()
                !isValidPassword(password) -> Toast.makeText(this, "Password must be at least 7 characters long, include an uppercase letter, a lowercase letter, a digit, and a special character.", Toast.LENGTH_LONG).show()
                else -> signUp(name, email, password)
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!])(?=.{7,}).*$"
        return password.matches(passwordPattern.toRegex())
    }

    private fun signUp(name: String, email: String, password: String) {
        pd.show()
        pd.setMessage("Signing Up")

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser

                val hashMap = hashMapOf(
                    "userid" to user!!.uid,
                    "image" to "https://upload.wikimedia.org/wikipedia/commons/2/2f/Google_account_icon.PNG",
                    "username" to name,
                    "email" to email,
                    "followers" to 0,
                    "following" to 0
                )

                firestore.collection("Users").document(user.uid).set(hashMap)
                pd.dismiss()

                startActivity(Intent(this, SignInActivity::class.java))
            } else {
                pd.dismiss()
                Toast.makeText(this, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
