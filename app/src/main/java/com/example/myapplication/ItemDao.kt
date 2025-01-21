package com.example.myapplication

import androidx.room.*

@Dao
interface ItemDao {

    // Insert a new item
    @Insert
    fun insert(item: Item)

    // Update an existing item
    @Update
    fun update(item: Item)

    // Delete an item
    @Delete
    fun delete(item: Item)

    // Get all items
    @Query("SELECT * FROM items")
    fun getAllItems(): List<Item>

    // Get an item by its ID
    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemById(id: Int): Item?
}
