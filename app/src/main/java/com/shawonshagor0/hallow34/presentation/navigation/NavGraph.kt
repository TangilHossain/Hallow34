package com.shawonshagor0.hallow34.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.shawonshagor0.hallow34.presentation.screens.AdminPanelScreen
import com.shawonshagor0.hallow34.presentation.screens.BpInputScreen
import com.shawonshagor0.hallow34.presentation.screens.EditUsersScreen
import com.shawonshagor0.hallow34.presentation.screens.ForgotPasswordScreen
import com.shawonshagor0.hallow34.presentation.screens.HomeScreen
import com.shawonshagor0.hallow34.presentation.screens.LauncherScreen
import com.shawonshagor0.hallow34.presentation.screens.ManageBannersScreen
import com.shawonshagor0.hallow34.presentation.screens.NotificationHistoryScreen
import com.shawonshagor0.hallow34.presentation.screens.ProfileScreen
import com.shawonshagor0.hallow34.presentation.screens.SendNotificationScreen
import com.shawonshagor0.hallow34.presentation.screens.SignupScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Launcher.route
    ) {

        // Launcher screen
        composable(Screen.Launcher.route) {
            LauncherScreen(navController)
        }

        // BP Input screen
        composable(Screen.BpInput.route) {
            BpInputScreen(navController)
        }

        // Forgot Password screen with bpNumber argument
        composable(
            route = Screen.ForgotPassword.route,
            arguments = listOf(navArgument("bpNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val bpNumber = backStackEntry.arguments?.getString("bpNumber") ?: ""
            ForgotPasswordScreen(navController, bpNumber)
        }

        // Signup screen with bpNumber argument
        composable(
            route = Screen.Signup.route,
            arguments = listOf(navArgument("bpNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val bpNumber = backStackEntry.arguments?.getString("bpNumber") ?: ""
            SignupScreen(navController, bpNumber)
        }

        // Login screen (optional, add later)
        composable(
            route = Screen.Login.route,
            arguments = listOf(navArgument("bpNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val bpNumber = backStackEntry.arguments?.getString("bpNumber") ?: ""
            // TODO: LoginScreen(navController, bpNumber)
        }

        // Home screen
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        // Send Notification screen
        composable(Screen.SendNotification.route) {
            SendNotificationScreen(navController)
        }

        // Notification History screen
        composable(Screen.NotificationHistory.route) {
            NotificationHistoryScreen(navController)
        }

        // Profile screen
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        // Admin Panel screen
        composable(Screen.AdminPanel.route) {
            AdminPanelScreen(navController)
        }

        // Edit Users screen
        composable(Screen.EditUsers.route) {
            EditUsersScreen(navController)
        }

        // Manage Banners screen
        composable(Screen.ManageBanners.route) {
            ManageBannersScreen(navController)
        }
    }
}
