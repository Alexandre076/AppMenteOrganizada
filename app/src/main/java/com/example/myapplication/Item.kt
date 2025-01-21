package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items") // Define this class as a Room Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generated primary key
    val name: String,   // Column for the name of the item
    val description: String  // Column for the description of the item
)
