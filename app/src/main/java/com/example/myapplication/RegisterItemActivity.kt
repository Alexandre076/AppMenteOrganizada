package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore


class RegisterItemActivity : AppCompatActivity() {

    // Firestore instance
    private lateinit var db: FirebaseFirestore

    private lateinit var itemNameEditText: EditText
    private lateinit var itemDescriptionEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_item)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize UI components
        itemNameEditText = findViewById(R.id.editTextItemName)
        itemDescriptionEditText = findViewById(R.id.editTextItemDescription)
        registerButton = findViewById(R.id.buttonRegister)

        // Register item on button click
        registerButton.setOnClickListener {
            val name = itemNameEditText.text.toString().trim()
            val description = itemDescriptionEditText.text.toString().trim()

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save item to Firestore
            saveItemToFirestore(name, description)
        }
    }

    private fun saveItemToFirestore(name: String, description: String) {
        val item = hashMapOf(
            "name" to name,
            "description" to description,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("items")
            .add(item)
            .addOnSuccessListener {documentReference ->
                Toast.makeText(this, "Item registered successfully!", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "Document added with ID: ${documentReference.id}")
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.w("Firestore", "Error adding document", e)
            }
    }

    private fun clearFields() {
        itemNameEditText.text.clear()
        itemDescriptionEditText.text.clear()
    }
}
