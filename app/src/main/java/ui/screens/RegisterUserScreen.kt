package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun RegisterUserScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
            navController.navigate("mainMenu")  // Navigate to Main Menu after registration
        }) {
            Text(text = "Register and proceed to Main Menu")
        }
    }
}
