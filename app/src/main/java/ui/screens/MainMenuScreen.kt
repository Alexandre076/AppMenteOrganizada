package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun MainMenuScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
            navController.navigate("registerItem")  // Navigate to Register Item
        }) {
            Text(text = "Register Item")
        }

        Button(onClick = {
            navController.navigate("checkItem")  // Navigate to Check Item
        }) {
            Text(text = "Check Items")
        }
    }
}
