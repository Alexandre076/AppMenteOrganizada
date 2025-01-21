package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        auth = FirebaseAuth.getInstance()

        val usernameField = findViewById<EditText>(R.id.editTextRegisterUsername)
        val passwordField = findViewById<EditText>(R.id.editTextRegisterPassword)
        val registerButton = findViewById<Button>(R.id.buttonRegisterSubmit)

        registerButton.setOnClickListener {
            val email = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (!isInputValid(email, password)) return@setOnClickListener

            registerUser(email, password)
        }
    }

    private fun isInputValid(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun registerUser(email: String, password: String) {
        val progressDialog = AlertDialog.Builder(this)
            .setView(R.layout.progress_dialog)
            .setCancelable(false)
            .create()
        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = auth.currentUser

                    if (userId != null && user != null) {
                        saveUserToDatabase(userId, email)

                        user.sendEmailVerification()
                            .addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Registration successful! A verification email has been sent. Please verify your email before logging in.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    // Log out user immediately to enforce email verification
                                    auth.signOut()

                                    redirectToLogin()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to send verification email. Please try again later.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Registration failed. User ID is null.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    handleRegistrationError(task.exception)
                }
            }
    }

    private fun handleRegistrationError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> Toast.makeText(this, "Email already registered.", Toast.LENGTH_SHORT).show()
            is FirebaseAuthWeakPasswordException -> Toast.makeText(this, "Weak password. Please choose a stronger password.", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, "Registration failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserToDatabase(userId: String, email: String) {
        val db = FirebaseFirestore.getInstance()
        val user = mapOf(
            "email" to email,
            "createdAt" to System.currentTimeMillis()
        )
        db.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User saved successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
