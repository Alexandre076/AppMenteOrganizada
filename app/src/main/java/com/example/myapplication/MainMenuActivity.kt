package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MainMenuActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var quoteTextView: TextView

    // List to hold tasks
    private val tasks: MutableList<Task> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // Button to navigate to Add Task Activity
        val addTaskButton = findViewById<Button>(R.id.buttonAddTask)
        addTaskButton.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, 1) // Use requestCode 1 for identifying this result
        }

        // Button to navigate to View Tasks Activity
        val viewTasksButton = findViewById<Button>(R.id.buttonViewTasks)
        viewTasksButton.setOnClickListener {
            val intent = Intent(this, ViewTasksActivity::class.java)
            intent.putParcelableArrayListExtra("tasks", ArrayList(tasks))
            startActivityForResult(intent, REQUEST_VIEW_TASKS)
        }

        //pull quote from zen api
        quoteTextView = findViewById(R.id.textViewQuote)
        fetchRandomQuote()

        // Button to logout
        val logoutButton = findViewById<Button>(R.id.buttonLogout)
        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putParcelableArrayListExtra("tasks", ArrayList(tasks))
            startActivity(intent)
        }
    }

    // Handle the result returned by AddTaskActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Get the new task from AddTaskActivity
            val newTasks: ArrayList<Task> = data?.getParcelableArrayListExtra("tasks")!!
            newTasks?.let {
                tasks.addAll(it)
                // Log the current list of tasks after the new task is added
                Log.d("MainMenuActivity", "Current Tasks List:")
                tasks.forEach { task ->
                    Log.d("MainMenuActivity", "Task ID: ${task.id}, Title: ${task.title}, Description: ${task.description}")
                }
            }
        }

        if (requestCode == REQUEST_VIEW_TASKS && resultCode == Activity.RESULT_OK) {
            val updatedTasks = data?.getParcelableArrayListExtra<Task>("updatedTasks")
            if (updatedTasks != null) {
                tasks.clear()
                tasks.addAll(updatedTasks)
                Log.d("MainMenuActivity", "Updated Tasks List:")
                tasks.forEach { task ->
                    Log.d("MainMenuActivity", "Task ID: ${task.id}, Title: ${task.title}")
                }
            }
        }
    }
    private fun fetchRandomQuote() {
        val request = Request.Builder()
            .url("https://zenquotes.io/api/random")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainMenuActivity,
                        "Failed to fetch quote: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = responseBody.string()
                    val jsonArray = JSONArray(json)
                    val quoteObject = jsonArray.getJSONObject(0)
                    val quote = quoteObject.getString("q")
                    // List of motivational keywords to filter quotes
                    val author = quoteObject.getString("a")

                    runOnUiThread {
                        quoteTextView.text = "\"$quote\" - $author"
                    }
                }
            }
        })
    }
    companion object {
        const val REQUEST_VIEW_TASKS = 2
    }
}
