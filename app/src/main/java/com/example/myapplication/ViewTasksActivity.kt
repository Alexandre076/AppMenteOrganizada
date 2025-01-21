package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewTasksActivity : AppCompatActivity() {

    private lateinit var tasks: MutableList<Task>
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tasks)

        recyclerView = findViewById(R.id.recyclerViewTasks)
        tasks = mutableListOf()

        // Initialize Firestore and Firebase Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize TaskAdapter with delete functionality
        taskAdapter = TaskAdapter(tasks,
            onTaskClick = { task ->
                val intent = Intent(this, AddTaskActivity::class.java)
                intent.putExtra("task", task)
                startActivityForResult(intent, EDIT_TASK_REQUEST_CODE)
            },
            onDelete = { task ->
                deleteTask(task)
            }
        )


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        // Fetch tasks from Firestore
        fetchTasksFromFirestore()
    }

    private fun fetchTasksFromFirestore() {
        val currentUser = auth.currentUser
        Log.d("ViewTasksActivity", "Fetching tasks for user: ${currentUser?.uid}")

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("users")
                .document(userId)
                .collection("tasks")
                .get()
                .addOnSuccessListener { documents ->
                    tasks.clear()
                    for (document in documents) {
                        Log.d("ViewTasksActivity", "Document: $document")
                        val task = document.toObject(Task::class.java)
                        task.id = document.id // Ensure the task has the Firestore document ID
                        tasks.add(task)
                    }
                    taskAdapter.notifyDataSetChanged()
                    Log.d("ViewTasksActivity", "Tasks fetched: $tasks")
                }
                .addOnFailureListener { e ->
                    Log.e("ViewTasksActivity", "Error fetching tasks", e)
                    Toast.makeText(this, "Error fetching tasks", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to delete a task from Firestore
    private fun deleteTask(task: Task) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("users")
                .document(userId)
                .collection("tasks")
                .document(task.id.toString())
                .delete()
                .addOnSuccessListener {
                    tasks.remove(task)
                    taskAdapter.notifyDataSetChanged()
                    Log.d("ViewTasksActivity", "Deleted task: ${task.title}")
                }
                .addOnFailureListener { e ->
                    Log.e("ViewTasksActivity", "Error deleting task", e)
                    Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fetchTasksFromFirestore() // Refresh the tasks list after edit
        }
    }

    companion object {
        const val EDIT_TASK_REQUEST_CODE = 2
    }
}
