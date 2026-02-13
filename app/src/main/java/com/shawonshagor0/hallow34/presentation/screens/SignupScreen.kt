package com.shawonshagor0.hallow34.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.shawonshagor0.hallow34.presentation.viewmodel.SignupState
import com.shawonshagor0.hallow34.presentation.viewmodel.SignupViewModel
import com.shawonshagor0.hallow34.presentation.utils.UnitDataProvider

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
    var mainUnit by remember { mutableStateOf("") }
    var subUnit by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var district by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var validationError by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var facebookProfileLink by remember { mutableStateOf("") }

    // Get main units and sub units from resources
    val mainUnits = remember { UnitDataProvider.getMainUnits(context) }
    val subUnits = remember(mainUnit) {
        if (mainUnit.isNotBlank()) {
            UnitDataProvider.getSubUnits(context, mainUnit)
        } else {
            emptyList()
        }
    }

    // Reset subUnit when mainUnit changes
    LaunchedEffect(mainUnit) {
        subUnit = ""
    }


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
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /* ---------- PROFILE IMAGE ---------- */

            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // Profile image circle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            // choose gallery first (simple UX)
                            galleryLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = profileImageUri),
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Placeholder with person icon
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Add Profile Picture",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Camera badge overlay (outside the clipped box)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Photo",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

//            Button(onClick = {
//                galleryLauncher.launch("image/*")
//            }) {
//                Text("Select Profile Picture")
//            }

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
                label = "Home District",
                options = listOf(
                    "ঢাকা", "ফরিদপুর", "গাজীপুর", "গোপালগঞ্জ", "কিশোরগঞ্জ", "মাদারীপুর",
                    "মানিকগঞ্জ", "মুন্সিগঞ্জ", "নারায়ণগঞ্জ", "নরসিংদী", "রাজবাড়ী",
                    "শরীয়তপুর", "টাঙ্গাইল",

                    "চট্টগ্রাম", "বান্দরবান", "ব্রাহ্মণবাড়িয়া", "চাঁদপুর", "কুমিল্লা", "কক্সবাজার",
                    "ফেনী", "খাগড়াছড়ি", "লক্ষ্মীপুর", "নোয়াখালী", "রাঙামাটি",

                    "রাজশাহী", "বগুড়া", "চাঁপাইনবাবগঞ্জ", "জয়পুরহাট", "নওগাঁ",
                    "নাটোর", "পাবনা", "সিরাজগঞ্জ",

                    "খুলনা", "বাগেরহাট", "চুয়াডাঙ্গা", "যশোর", "ঝিনাইদহ",
                    "কুষ্টিয়া", "মাগুরা", "মেহেরপুর", "নড়াইল", "সাতক্ষীরা",

                    "বরিশাল", "বরগুনা", "ভোলা", "ঝালকাঠি",
                    "পটুয়াখালী", "পিরোজপুর",

                    "সিলেট", "হবিগঞ্জ", "মৌলভীবাজার", "সুনামগঞ্জ",

                    "রংপুর", "দিনাজপুর", "গাইবান্ধা", "কুড়িগ্রাম",
                    "লালমনিরহাট", "নীলফামারী", "পঞ্চগড়", "ঠাকুরগাঁও",

                    "ময়মনসিংহ", "জামালপুর", "নেত্রকোনা", "শেরপুর"


                ),
                selectedValue = district,
                placeholder = "Select District",
                onValueChange = { district = it }
            )

            DropdownSelector(
                label = "Main Unit",
                options = mainUnits,
                selectedValue = mainUnit,
                placeholder = "Select Main Unit",
                onValueChange = { mainUnit = it }
            )

            if (subUnits.isNotEmpty()) {
                DropdownSelector(
                    label = "Sub Unit / Posting Place",
                    options = subUnits,
                    selectedValue = subUnit,
                    placeholder = "Select Sub Unit",
                    onValueChange = { subUnit = it }
                )
            }

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
                onValueChange = {
                    // Only allow digits and max 10 characters
                    if (it.length <= 11 && it.all { char -> char.isDigit() }) {
                        phone = it
                    }
                },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("+88 ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                supportingText = { Text("Enter 11 digit number") }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    validationError = ""
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    validationError = ""
                },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            OutlinedTextField(
                value = facebookProfileLink,
                onValueChange = { facebookProfileLink = it },
                label = { Text("Facebook Profile Link") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            /* ---------- SUBMIT ---------- */

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = signupState !is SignupState.Loading,
                onClick = {
                    // Validate all mandatory fields
                    when {
                        profileImageUri == null -> {
                            validationError = "Please select a profile picture"
                            return@Button
                        }
                        fullName.isBlank() -> {
                            validationError = "Full Name is required"
                            return@Button
                        }
                        designation.isBlank() -> {
                            validationError = "Designation is required"
                            return@Button
                        }
                        district.isBlank() -> {
                            validationError = "Please select a District"
                            return@Button
                        }
                        mainUnit.isBlank() -> {
                            validationError = "Please select a Main Unit"
                            return@Button
                        }
                        subUnits.isNotEmpty() && subUnit.isBlank() -> {
                            validationError = "Please select a Sub Unit / Posting Place"
                            return@Button
                        }
                        bloodGroup.isBlank() -> {
                            validationError = "Please select a Blood Group"
                            return@Button
                        }
                        phone.length != 11 -> {
                            validationError = "Phone Number must be exactly 11 digits"
                            return@Button
                        }
//                        email.isBlank() -> {
//                            validationError = "Email Address is required"
//                            return@Button
//                        }
//                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
//                            validationError = "Please enter a valid email address"
//                            return@Button
//                        }
                        password.length < 6 -> {
                            validationError = "Password must be at least 6 characters"
                            return@Button
                        }
                        password != confirmPassword -> {
                            validationError = "Passwords do not match"
                            return@Button
                        }
//                        facebookProfileLink.isBlank() -> {
//                            validationError = "Facebook Profile Link is required"
//                            return@Button
//                        }
                    }

                    viewModel.signupUser(
                        context = context,
                        bpNumber = bpNumber,
                        fullName = fullName,
                        designation = designation,
                        district = district,
                        postingPlace = subUnit.ifBlank { mainUnit },
                        bloodGroup = bloodGroup,
                        phone = phone,
                        email = email,
                        password = password,
                        facebookProfileLink = facebookProfileLink,
                        profileImageUri = profileImageUri,
                        onSuccess = {
                            navController.navigate("home") {
                                popUpTo("launcher") { inclusive = true }
                            }
                        },
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

            // Show validation error
            if (validationError.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = validationError,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Show error if any
            if (signupState is SignupState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (signupState as SignupState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
