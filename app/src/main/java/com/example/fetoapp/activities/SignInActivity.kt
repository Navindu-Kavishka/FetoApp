@file:Suppress("DEPRECATION")

package com.example.fetoapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.fetoapp.MainActivity
import com.example.fetoapp.R
import com.example.fetoapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var  pd:ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        binding.signInTextToSignUp.setOnClickListener{

            startActivity(Intent(this, SignUpActivity::class.java))
        }


        //if user already logged take to main activity
        if (auth.currentUser != null){

            startActivity(Intent(this, MainActivity::class.java))
        }

        pd = ProgressDialog(this)

        binding.loginButton.setOnClickListener {

            if (binding.loginetemail.text.isNotEmpty() && binding.loginetpassword.text.isNotEmpty()){

                val email = binding.loginetemail.text.toString()
                val password = binding.loginetpassword.text.toString()

                signIn(email, password)
            }

            if (binding.loginetemail.text.isEmpty() || binding.loginetpassword.text.isEmpty()){

                Toast.makeText(this, "Fill the Field First", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun signIn(email: String, password: String) {

        pd.show()
        pd.setMessage("Signing In")

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task->

            if (task.isSuccessful){

                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show()
                pd.dismiss()


            }
        }.addOnFailureListener {

            pd.dismiss()
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()

            return@addOnFailureListener

        }

    }
}