package com.shawonshagor0.hallow34.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.shawonshagor0.hallow34.presentation.viewmodel.LoginViewModel

@Composable
fun BpInputScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var bpNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var userExists by remember { mutableStateOf(false) }
    var storedPassword by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }

    // Check for auto-login on first composition
    LaunchedEffect(Unit) {
        if (viewModel.checkAutoLogin()) {
            navController.navigate("home") {
                popUpTo("launcher") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (userExists) "Welcome Back!" else "Enter Your BP Number",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = bpNumber,
            onValueChange = {
                bpNumber = it
                // Reset state when BP number changes
                if (userExists) {
                    userExists = false
                    password = ""
                    error = ""
                }
            },
            label = { Text("BP Number") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !userExists && !isLoading,
            singleLine = true
        )

        // Show password field when user exists
        if (userExists) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    error = ""
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Remember Me checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it }
                )
                Text(
                    text = "Remember Me",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                error = ""
                if (bpNumber.isBlank()) {
                    error = "BP Number cannot be empty"
                    return@Button
                }

                if (userExists) {
                    // Verify password
                    if (password.isBlank()) {
                        error = "Password cannot be empty"
                        return@Button
                    }
                    if (password == storedPassword) {
                        // Password matches - save session and go to home
                        isLoading = true
                        viewModel.saveSession(bpNumber, rememberMe) {
                            navController.navigate("home") {
                                popUpTo("launcher") { inclusive = true }
                            }
                        }
                    } else {
                        error = "Incorrect password. Please try again."
                    }
                } else {
                    // Check if user exists
                    isLoading = true
                    checkUserExists(
                        bpNumber = bpNumber,
                        onUserFound = { foundPassword ->
                            isLoading = false
                            userExists = true
                            storedPassword = foundPassword
                        },
                        onUserNotFound = {
                            isLoading = false
                            navController.navigate("signup/$bpNumber")
                        },
                        onError = { errorMsg ->
                            isLoading = false
                            error = errorMsg
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (userExists) "Login" else "Continue")
            }
        }

        // Option to change BP number when in login mode
        if (userExists) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    userExists = false
                    password = ""
                    error = ""
                    bpNumber = ""
                }
            ) {
                Text("Use different BP Number")
            }
        }

        if (error.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Firestore check with password retrieval
private fun checkUserExists(
    bpNumber: String,
    onUserFound: (password: String) -> Unit,
    onUserNotFound: () -> Unit,
    onError: (String) -> Unit
) {
    val docRef = FirebaseFirestore.getInstance()
        .collection("users")
        .document(bpNumber)

    docRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val password = document.getString("password") ?: ""
                onUserFound(password)
            } else {
                onUserNotFound()
            }
        }
        .addOnFailureListener { e ->
            onError("Error: ${e.message}")
        }
}
