package com.shawonshagor0.hallow34.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import coil.compose.SubcomposeAsyncImage
import com.shawonshagor0.hallow34.domain.model.User
import com.shawonshagor0.hallow34.presentation.components.UserCard
import com.shawonshagor0.hallow34.presentation.navigation.Screen
import com.shawonshagor0.hallow34.presentation.viewmodel.HomeViewModel
import com.shawonshagor0.hallow34.presentation.viewmodel.LoginViewModel
import com.shawonshagor0.hallow34.ui.theme.GradientEnd
import com.shawonshagor0.hallow34.ui.theme.GradientStart
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedUser by remember { mutableStateOf<User?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                // Drawer Header with Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(GradientStart, GradientEnd)
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Text(
                            text = "Hallow34",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Police Directory",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // My Profile
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "My Profile",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    label = {
                        Text(
                            "My Profile",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Profile.route)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Send Notification
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Send Notification",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    label = {
                        Text(
                            "Send Notification",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.SendNotification.route)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Notification History
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Notification History",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    label = {
                        Text(
                            "Notification History",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.NotificationHistory.route)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                Spacer(modifier = Modifier.height(8.dp))

                // Logout
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    label = {
                        Text(
                            "Logout",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        loginViewModel.logout {
                            navController.navigate(Screen.Launcher.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Directory",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = viewModel.searchQuery,
                    onValueChange = viewModel::onSearchChange,
                    placeholder = {
                        Text(
                            "Search by name, phone, blood group...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )

                // Results count
                Text(
                    text = "${viewModel.filteredUsers.size} members found",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                // User List with Pull to Refresh
                PullToRefreshBox(
                    isRefreshing = viewModel.isRefreshing,
                    onRefresh = { viewModel.refreshUsers() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.filteredUsers) { user ->
                            UserCard(
                                user = user,
                                onClick = { selectedUser = user }
                            )
                        }
                    }
                }
            }
        }
    }

    // User Details Bottom Sheet
    selectedUser?.let { user ->
        ModalBottomSheet(
            onDismissRequest = { selectedUser = null },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            UserDetailsSheet(
                user = user,
                onDismiss = { selectedUser = null }
            )
        }
    }
}

@Composable
private fun UserDetailsSheet(
    user: User,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Profile Image and Name
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    )
                    .padding(3.dp)
            ) {
                SubcomposeAsyncImage(
                    model = user.imageUrl.ifEmpty { null },
                    contentDescription = "Profile",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "No Image",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = user.designation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "BP: ${user.bpNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        // Details
        DetailRow(label = "Phone", value = user.phone)
        DetailRow(label = "Email", value = user.email)
        DetailRow(label = "District", value = user.district)
        DetailRow(label = "Current Range", value = user.currentRange)
        DetailRow(label = "Blood Group", value = user.bloodGroup)

        Spacer(modifier = Modifier.height(24.dp))

        // Call Button
        Button(
            onClick = {
                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                    data = android.net.Uri.parse("tel:${user.phone}")
                }
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Call,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Call ${user.fullName.split(" ").firstOrNull() ?: ""}",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
}
