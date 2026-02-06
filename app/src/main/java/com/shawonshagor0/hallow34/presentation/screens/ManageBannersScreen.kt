package com.shawonshagor0.hallow34.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shawonshagor0.hallow34.domain.model.Banner
import com.shawonshagor0.hallow34.presentation.viewmodel.BannerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBannersScreen(
    navController: NavController,
    viewModel: BannerViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var bannerMessage by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var bannerToDelete by remember { mutableStateOf<Banner?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Show messages
    LaunchedEffect(viewModel.successMessage, viewModel.errorMessage) {
        if (viewModel.successMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(viewModel.successMessage)
            viewModel.clearMessages()
        }
        if (viewModel.errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(viewModel.errorMessage)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Manage Banners",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Banner"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Active Banners",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create banners to show announcements on the home screen",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.banners.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No banners yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to add a new banner",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.banners) { banner ->
                        BannerCard(
                            banner = banner,
                            onDelete = {
                                bannerToDelete = banner
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Banner Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                bannerMessage = ""
            },
            title = { Text("Add New Banner") },
            text = {
                OutlinedTextField(
                    value = bannerMessage,
                    onValueChange = { bannerMessage = it },
                    label = { Text("Banner Message") },
                    placeholder = { Text("Enter announcement message...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createBanner(bannerMessage)
                        showAddDialog = false
                        bannerMessage = ""
                    },
                    enabled = bannerMessage.isNotBlank()
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    bannerMessage = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && bannerToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                bannerToDelete = null
            },
            title = { Text("Delete Banner") },
            text = { Text("Are you sure you want to delete this banner? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        bannerToDelete?.let { viewModel.deleteBanner(it.id) }
                        showDeleteDialog = false
                        bannerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    bannerToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BannerCard(
    banner: Banner,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(banner.createdAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = banner.message,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Created: $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
