package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// TaskAdapter.kt
class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskClick: (Task) -> Unit, // Pass a function to handle task clicks
    private val onDelete: (Task) -> Unit   // Callback for delete action
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTaskTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewTaskDescription)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleTextView.text = task.title
        holder.descriptionTextView.text = task.description

        // Set a click listener on the entire task item
        holder.itemView.setOnClickListener {
            onTaskClick(task)
        }

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            onDelete(task) // Invoke the delete callback
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}
