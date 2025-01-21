package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun RegisterItemScreen(navController: NavHostController) {
    // UI for registering an item
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Register Item Screen")
        // Registration form can go here
    }
}
