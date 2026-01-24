package com.shawonshagor0.hallow34.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.shawonshagor0.hallow34.presentation.screens.BpInputScreen
import com.shawonshagor0.hallow34.presentation.screens.LauncherScreen
import com.shawonshagor0.hallow34.presentation.screens.SignupScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Launcher.route
    ) {
        composable(Screen.Launcher.route) {
            LauncherScreen(navController)
        }
        composable(Screen.BpInput.route){
            BpInputScreen(navController)
        }
        composable(
            route = Screen.Signup.route,
            arguments = listOf(navArgument("bpNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val bpNumber = backStackEntry.arguments?.getString("bpNumber") ?: ""
            SignupScreen(navController, bpNumber)
        }

        // Later we will add BpInput, Login, Signup, Home screens here
    }
}