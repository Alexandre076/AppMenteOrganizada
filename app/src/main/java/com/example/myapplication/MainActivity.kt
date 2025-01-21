package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_login) // Set the login XML layout



        // Set up button listeners for navigation
        val loginButton: Button = findViewById(R.id.buttonLogin)
        val registerButton: TextView = findViewById(R.id.buttonRegister)

        // Navigate to the main menu after login
        loginButton.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
        }

        // Navigate to the register user screen
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
