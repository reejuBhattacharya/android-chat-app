package com.example.reejubhai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.reejubhai.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    companion object {
        val TAG = "LoginActivity"
    }

    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLogin()

        binding.backtosignupTextView.setOnClickListener {
            finish()
        }
    }

    private fun setupLogin()
    {
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmailEdittext.text.toString()
            val password = binding.loginPasswordEdittext.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Log.d(TAG, "logged in successfully")
                    val intent = Intent(this, NewestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d(TAG, it.message!!)
                }
        }
    }
}
