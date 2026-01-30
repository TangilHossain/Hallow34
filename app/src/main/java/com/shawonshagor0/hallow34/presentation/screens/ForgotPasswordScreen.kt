package com.shawonshagor0.hallow34.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shawonshagor0.hallow34.ui.theme.GradientEnd
import com.shawonshagor0.hallow34.ui.theme.GradientStart
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var bpNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var success by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forgot Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientStart,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Card
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
                        Icon(
                            imageVector = if (success) Icons.Default.Check else Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (success) "Email Sent!" else "Reset Password",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (success)
                                successMessage.ifBlank { "A password reset link has been sent to your registered email. Please check your inbox." }
                            else
                                "Enter your BP Number and we'll send a password reset link to your registered email.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        if (success) {
                            // Success state - show back button
                            Button(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Back to Login",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        } else {
                            // BP Number Field
                            OutlinedTextField(
                                value = bpNumber,
                                onValueChange = {
                                    bpNumber = it
                                    error = ""
                                },
                                label = { Text("BP Number") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    error = ""

                                    if (bpNumber.isBlank()) {
                                        error = "Please enter your BP Number"
                                        return@Button
                                    }

                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val result = sendPasswordResetEmail(bpNumber)
                                            isLoading = false
                                            if (result.first) {
                                                success = true
                                                successMessage = result.second
                                            } else {
                                                error = result.second
                                            }
                                        } catch (e: Exception) {
                                            isLoading = false
                                            error = e.message ?: "An error occurred"
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = !isLoading,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Send Reset Link",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }

                            // Error Message
                            AnimatedVisibility(visible = error.isNotBlank()) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private suspend fun sendPasswordResetEmail(bpNumber: String): Pair<Boolean, String> {
    return withContext(Dispatchers.IO) {
        try {
            // First check if the user exists in Firestore and get their real email
            val docRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(bpNumber)

            val document = docRef.get().await()

            if (!document.exists()) {
                return@withContext Pair(false, "No account found with this BP Number")
            }

            // Get the real email from Firestore
            val realEmail = document.getString("email") ?: ""

            if (realEmail.isBlank()) {
                return@withContext Pair(false, "No email address registered for this account")
            }

            // Send password reset email to the REAL email address
            FirebaseAuth.getInstance().sendPasswordResetEmail(realEmail).await()

            // Mask the email for display (e.g., s***@gmail.com)
            val maskedEmail = maskEmail(realEmail)
            Pair(true, "Password reset link sent to $maskedEmail")

        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("no user record") == true ->
                    "No account found with this BP Number"
                e.message?.contains("INVALID_EMAIL") == true ->
                    "Invalid email address format"
                else -> e.message ?: "Failed to send reset email"
            }
            Pair(false, errorMessage)
        }
    }
}

// Helper function to mask email for privacy
private fun maskEmail(email: String): String {
    val parts = email.split("@")
    if (parts.size != 2) return email

    val name = parts[0]
    val domain = parts[1]

    val maskedName = if (name.length <= 2) {
        name.first() + "*".repeat(name.length - 1)
    } else {
        name.first() + "*".repeat(name.length - 2) + name.last()
    }

    return "$maskedName@$domain"
}

