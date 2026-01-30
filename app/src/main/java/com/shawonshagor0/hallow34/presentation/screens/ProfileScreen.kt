package com.shawonshagor0.hallow34.presentation.screens

import android.net.Uri
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.shawonshagor0.hallow34.presentation.viewmodel.ProfileState
import com.shawonshagor0.hallow34.presentation.viewmodel.ProfileViewModel
import com.shawonshagor0.hallow34.presentation.utils.UnitDataProvider
import com.shawonshagor0.hallow34.ui.theme.GradientEnd
import com.shawonshagor0.hallow34.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Editable fields
    var isEditing by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var mainUnit by remember { mutableStateOf("") }
    var subUnit by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var facebookProfileLink by remember { mutableStateOf("") }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }

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
        if (isEditing) {
            subUnit = ""
        }
    }

    // Image picker
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        newImageUri = uri
    }

    // Initialize fields when user data loads
    LaunchedEffect(user) {
        user?.let {
            fullName = it.fullName
            designation = it.designation
            district = it.district
            bloodGroup = it.bloodGroup
            phone = it.phone
            email = it.email
            facebookProfileLink = it.facebookProfileLink

            // Parse currentRange to find mainUnit and subUnit
            val storedRange = it.currentRange
            val allMainUnits = UnitDataProvider.getMainUnits(context)

            if (storedRange in allMainUnits) {
                mainUnit = storedRange
                subUnit = ""
            } else {
                var found = false
                for (unit in allMainUnits) {
                    val subs = UnitDataProvider.getSubUnits(context, unit)
                    if (storedRange in subs) {
                        mainUnit = unit
                        subUnit = storedRange
                        found = true
                        break
                    }
                }
                if (!found && storedRange.isNotBlank()) {
                    subUnit = storedRange
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Profile",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (state !is ProfileState.Loading && state !is ProfileState.Updating) {
                        IconButton(
                            onClick = {
                                if (isEditing) {
                                    // Save changes
                                    viewModel.updateProfile(
                                        context = context,
                                        fullName = fullName,
                                        designation = designation,
                                        district = district,
                                        currentRange = subUnit.ifBlank { mainUnit },
                                        bloodGroup = bloodGroup,
                                        phone = phone,
                                        email = email,
                                        facebookProfileLink = facebookProfileLink,
                                        newImageUri = newImageUri,
                                        onSuccess = {
                                            isEditing = false
                                            newImageUri = null
                                        }
                                    )
                                } else {
                                    isEditing = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = if (isEditing) "Save" else "Edit",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when (state) {
            is ProfileState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProfileState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (state as ProfileState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadUserProfile() }) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                ) {
                    // Profile Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(GradientStart, GradientEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = newImageUri ?: user?.imageUrl ?: "https://via.placeholder.com/150"
                                ),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .clickable(enabled = isEditing) {
                                        galleryLauncher.launch("image/*")
                                    },
                                contentScale = ContentScale.Crop
                            )

                            if (isEditing) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .align(Alignment.BottomEnd)
                                        .clickable { galleryLauncher.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircle,
                                        contentDescription = "Change Photo",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // BP Number (non-editable)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-24).dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "BP Number",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = user?.bpNumber ?: "",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Loading indicator while updating
                    if (state is ProfileState.Updating) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Profile Fields
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileTextField(
                            label = "Full Name",
                            value = fullName,
                            onValueChange = { fullName = it },
                            enabled = isEditing
                        )

                        ProfileTextField(
                            label = "Designation",
                            value = designation,
                            onValueChange = { designation = it },
                            enabled = isEditing
                        )

                        if (isEditing) {
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
                        } else {
                            ProfileTextField(
                                label = "District",
                                value = district,
                                onValueChange = { },
                                enabled = false
                            )
                        }

                        // Main Unit and Sub Unit dropdowns (same as SignupScreen)
                        if (isEditing) {
                            DropdownSelector(
                                label = "Main Unit",
                                options = mainUnits,
                                selectedValue = mainUnit,
                                placeholder = "Select Main Unit",
                                onValueChange = { mainUnit = it }
                            )

                            if (subUnits.isNotEmpty()) {
                                DropdownSelector(
                                    label = "Sub Unit / Current Range",
                                    options = subUnits,
                                    selectedValue = subUnit,
                                    placeholder = "Select Sub Unit",
                                    onValueChange = { subUnit = it }
                                )
                            }
                        } else {
                            ProfileTextField(
                                label = "Current Range",
                                value = subUnit.ifBlank { mainUnit },
                                onValueChange = { },
                                enabled = false
                            )
                        }

                        if (isEditing) {
                            DropdownSelector(
                                label = "Blood Group",
                                options = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"),
                                selectedValue = bloodGroup,
                                placeholder = "Select Blood Group",
                                onValueChange = { bloodGroup = it }
                            )
                        } else {
                            ProfileTextField(
                                label = "Blood Group",
                                value = bloodGroup,
                                onValueChange = { },
                                enabled = false
                            )
                        }

                        ProfileTextField(
                            label = "Phone",
                            value = phone,
                            onValueChange = { phone = it },
                            enabled = isEditing
                        )

                        ProfileTextField(
                            label = "Email",
                            value = email,
                            onValueChange = { email = it },
                            enabled = isEditing
                        )

                        ProfileTextField(
                            label = "Facebook Profile Link",
                            value = facebookProfileLink,
                            onValueChange = { facebookProfileLink = it },
                            enabled = isEditing
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
