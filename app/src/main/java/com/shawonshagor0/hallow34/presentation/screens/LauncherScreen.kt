package com.shawonshagor0.hallow34.presentation.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.shawonshagor0.hallow34.presentation.navigation.Screen
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect

@Composable
fun LauncherScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Hallow34")
    }

    // Auto navigate to BP Input screen after 2 seconds
    LaunchedEffect(Unit) {
        delay(200)
        navController.navigate(Screen.BpInput.route)
    }
}