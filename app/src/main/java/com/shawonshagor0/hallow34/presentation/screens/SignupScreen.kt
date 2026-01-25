package com.shawonshagor0.hallow34.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.shawonshagor0.hallow34.presentation.viewmodel.SignupState
import com.shawonshagor0.hallow34.presentation.viewmodel.SignupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    navController: NavController,
    bpNumber: String,
    viewModel: SignupViewModel = hiltViewModel()
) {
    /* -------------------- STATE -------------------- */
    val signupState by viewModel.state.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var currentRange by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var district by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current


    /* ---------------- IMAGE PICKERS ---------------- */

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            profileImageUri = it
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                val uri = Uri.parse(
                    android.provider.MediaStore.Images.Media.insertImage(
                        context.contentResolver,
                        it,
                        "profile_image",
                        null
                    )
                )
                profileImageUri = uri
            }
        }


    /* -------------------- UI -------------------- */

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Sign Up") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /* ---------- PROFILE IMAGE ---------- */

            Image(
                painter = rememberAsyncImagePainter(
                    model = profileImageUri ?: "https://via.placeholder.com/150"
                ),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable {
                        // choose gallery first (simple UX)
                        galleryLauncher.launch("image/*")
                    },
                contentScale = ContentScale.Crop
            )

            TextButton(onClick = {
                galleryLauncher.launch("image/*")
            }) {
                Text("Select Profile Picture")
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------- TEXT INPUTS ---------- */

            OutlinedTextField(
                value = bpNumber,
                onValueChange = {},
                label = { Text("BP Number") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = designation,
                onValueChange = { designation = it },
                label = { Text("Designation") },
                modifier = Modifier.fillMaxWidth()
            )

            /* ---------- DROPDOWNS ---------- */

            DropdownSelector(
                label = "District",
                options = listOf(
                    "Dhaka", "Chittagong", "Rajshahi",
                    "Khulna", "Barishal", "Sylhet", "Rangpur", "Mymensingh"
                ),
                selectedValue = district,
                placeholder = "Select District",
                onValueChange = { district = it }
            )

            OutlinedTextField(
                value = currentRange,
                onValueChange = { currentRange = it },
                label = { Text("Current Range") },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownSelector(
                label = "Blood Group",
                options = listOf(
                    "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"
                ),
                selectedValue = bloodGroup,
                placeholder = "Select Blood Group",
                onValueChange = { bloodGroup = it }
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            /* ---------- SUBMIT ---------- */

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = signupState !is SignupState.Loading,
                onClick = {
                    viewModel.signupUser(
                        context = context,
                        bpNumber = bpNumber,
                        fullName = fullName,
                        designation = designation,
                        district = district,
                        currentRange = currentRange,
                        bloodGroup = bloodGroup,
                        phone = phone,
                        email = email,
                        profileImageUri = profileImageUri,
                        onSuccess = {
                            navController.navigate("home") {
                                popUpTo("launcher") { inclusive = true }
                            }
                        }
                    )
                }
            ) {
                if (signupState is SignupState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Account")
                }
            }

            // Show error if any
            if (signupState is SignupState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (signupState as SignupState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
