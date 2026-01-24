package com.shawonshagor0.hallow34.presentation.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shawonshagor0.hallow34.presentation.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun BpInputScreen(navController: NavController) {
    var bpNumber by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = bpNumber,
            onValueChange = { bpNumber = it },
            label = { Text("Enter BP Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (bpNumber.isBlank()) {
                    error = "BP Number cannot be empty"
                } else {
                    checkUserExists(bpNumber, navController)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }

        if (error.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error, color = androidx.compose.ui.graphics.Color.Red)
        }
    }
}

// Function to check if user exists in Firestore
private fun checkUserExists(bpNumber: String, navController: NavController) {
    val docRef = FirebaseFirestore.getInstance()
        .collection("users")
        .document(bpNumber)

    docRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            // User exists → go to Login screen
            navController.navigate(Screen.Login.route + "/$bpNumber")
        } else {
            // User does not exist → go to Signup screen
            navController.navigate(Screen.Signup.route + "/$bpNumber")
        }
    }.addOnFailureListener {
        // Optional: show error
    }
}