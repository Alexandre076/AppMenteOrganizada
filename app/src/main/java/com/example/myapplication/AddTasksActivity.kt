package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddTaskActivity : AppCompatActivity() {

    private var task: Task? = null // Variable to hold the task being edited, if any
    private val taskList = mutableListOf<Task>()
    private val addedTasks = mutableListOf<Task>()

    // Firestore instance
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val taskNameField = findViewById<EditText>(R.id.task_name)
        val taskDescriptionField = findViewById<EditText>(R.id.task_description)
        val saveButton = findViewById<Button>(R.id.save_button)
        val backButton = findViewById<Button>(R.id.buttonBackToMainMenu)

        // Check if a task is being passed in (edit mode)
        task = intent.getParcelableExtra("task")

        if (task != null) {
            // Prepopulate the fields in edit mode
            taskNameField.setText(task?.title)
            taskDescriptionField.setText(task?.description)
        }

        saveButton.setOnClickListener {
            val taskName = taskNameField.text.toString().trim()
            val taskDescription = taskDescriptionField.text.toString().trim()

            if (taskName.isEmpty() || taskDescription.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedTask = if (task != null) {
                // Edit mode: create a new Task object with the updated information
                Task(task!!.id, taskName, taskDescription, task!!.isCompleted)
            } else {
                // Add mode: create a new task
                Task("", taskName, taskDescription, false) // Empty ID, Firestore will generate it
            }

            // Save the task to Firestore
            saveTaskToFirestore(updatedTask)

            addedTasks.add(updatedTask)
            taskList.add(updatedTask)

            // Clear the fields for a new entry
            taskNameField.text.clear()
            taskDescriptionField.text.clear()
        }

        // Back button returns to MainMenuActivity
        backButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putParcelableArrayListExtra("tasks", ArrayList(addedTasks))
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun saveTaskToFirestore(task: Task) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            val taskData = hashMapOf(
                "title" to task.title,
                "description" to task.description,
                "isCompleted" to task.isCompleted,
                "timestamp" to System.currentTimeMillis() // Use current timestamp when adding the task
            )

            // Save the task under the user's document
            db.collection("users")
                .document(userId)
                .collection("tasks")
                .add(taskData) // Firestore generates the ID automatically
                .addOnSuccessListener { documentReference ->
                    // Set the Firestore generated ID
                    task.id = documentReference.id

                    // Prepare the updated task data
                    val updatedTaskData: MutableMap<String, Any> = hashMapOf(
                        "id" to task.id,
                        "title" to task.title,
                        "description" to task.description,
                        "isCompleted" to task.isCompleted,
                        "timestamp" to task.timestamp // Now this field exists in the Task object
                    )

                    // Explicit cast to MutableMap<String, Any>
                    documentReference.update(updatedTaskData as MutableMap<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Task saved to Firestore!", Toast.LENGTH_SHORT).show()
                            Log.d("AddTaskActivity", "Task saved: $task")
                        }
                        .addOnFailureListener { e ->
                            Log.e("AddTaskActivity", "Failed to update task ID", e)
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving task: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AddTaskActivity", "Error: ${e.message}", e)
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

}
