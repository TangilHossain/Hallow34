package com.shawonshagor0.hallow34.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.shawonshagor0.hallow34.presentation.viewmodel.LoginViewModel
import com.shawonshagor0.hallow34.ui.theme.GradientEnd
import com.shawonshagor0.hallow34.ui.theme.GradientStart

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
    var rememberMe by remember { mutableStateOf(true) }

    // Check for auto-login on first composition
    LaunchedEffect(Unit) {
        if (viewModel.checkAutoLogin()) {
            navController.navigate("home") {
                popUpTo("launcher") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // App Logo/Title
            Text(
                text = "Hallow34",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Bangladesh Police Directory",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (userExists) "Welcome Back!" else "Sign In",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (userExists) "Enter your password to continue"
                               else "Enter your BP Number to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // BP Number Field
                    OutlinedTextField(
                        value = bpNumber,
                        onValueChange = {
                            // Only allow digits and max 10 characters
                            if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                bpNumber = it
                                if (userExists) {
                                    userExists = false
                                    password = ""
                                    error = ""
                                }
                            }
                        },
                        label = { Text("BP Number") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !userExists && !isLoading,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        supportingText = { Text("Must start with 8 and be 10 digits") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    // Animated Password Field
                    AnimatedVisibility(
                        visible = userExists,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    error = ""
                                },
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Remember Me
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = "Remember me",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                // Forgot Password
                                TextButton(
                                    onClick = { navController.navigate("forgot_password/$bpNumber") }
                                ) {
                                    Text(
                                        text = "Forgot Password?",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login/Continue Button
                    Button(
                        onClick = {
                            error = ""
                            // Validate BP Number format
                            when {
                                bpNumber.isBlank() -> {
                                    error = "BP Number cannot be empty"
                                    return@Button
                                }
                                bpNumber.length != 10 -> {
                                    error = "BP Number must be exactly 10 digits"
                                    return@Button
                                }
                                !bpNumber.startsWith("8") -> {
                                    error = "BP Number must start with 8"
                                    return@Button
                                }
                            }

                            if (userExists) {
                                // Use Firebase Auth to sign in
                                if (password.isBlank()) {
                                    error = "Password cannot be empty"
                                    return@Button
                                }

                                isLoading = true
                                viewModel.signIn(
                                    bpNumber = bpNumber,
                                    password = password,
                                    rememberMe = rememberMe,
                                    onSuccess = {
                                        navController.navigate("home") {
                                            popUpTo("launcher") { inclusive = true }
                                        }
                                    },
                                    onError = { errorMsg ->
                                        isLoading = false
                                        error = when {
                                            errorMsg.contains("password is invalid") ||
                                            errorMsg.contains("INVALID_LOGIN_CREDENTIALS") ->
                                                "Incorrect password. Please try again."
                                            errorMsg.contains("no user record") ->
                                                "Account not found. Please sign up."
                                            else -> errorMsg
                                        }
                                    }
                                )
                            } else {
                                // Check if user exists in Firestore
                                isLoading = true
                                checkUserExists(
                                    bpNumber = bpNumber,
                                    onUserFound = {
                                        isLoading = false
                                        userExists = true
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (userExists) "Login" else "Continue",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    // Change BP Number option
                    AnimatedVisibility(visible = userExists) {
                        TextButton(
                            onClick = {
                                userExists = false
                                password = ""
                                error = ""
                                bpNumber = ""
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                "Use different BP Number",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Error Message
                    AnimatedVisibility(visible = error.isNotBlank()) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// Firestore check - now only checks if user profile exists (not password)
private fun checkUserExists(
    bpNumber: String,
    onUserFound: () -> Unit,
    onUserNotFound: () -> Unit,
    onError: (String) -> Unit
) {
    val docRef = FirebaseFirestore.getInstance()
        .collection("users")
        .document(bpNumber)

    docRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                onUserFound()
            } else {
                onUserNotFound()
            }
        }
        .addOnFailureListener { e ->
            onError("Error: ${e.message}")
        }
}
