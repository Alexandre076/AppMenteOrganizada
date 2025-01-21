package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CheckItemActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var itemDao: ItemDao
    private lateinit var itemsTextView: TextView
    private lateinit var loadItemsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_item)

        // Get the database and DAO instance
        database = AppDatabase.getDatabase(this)
        itemDao = database.itemDao()

        // Initialize UI components
        itemsTextView = findViewById(R.id.textViewItems)
        loadItemsButton = findViewById(R.id.buttonLoadItems)

        // Load items on button click
        loadItemsButton.setOnClickListener {
            lifecycleScope.launch {
                val items = itemDao.getAllItems()
                displayItems(items)
            }
        }
    }

    private fun displayItems(items: List<Item>) {
        val itemsList = items.joinToString("\n") { "${it.name}: ${it.description}" }
        itemsTextView.text = if (itemsList.isEmpty()) "No items found." else itemsList
    }
}
