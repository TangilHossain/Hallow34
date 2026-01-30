package com.shawonshagor0.hallow34.presentation.navigation

sealed class Screen(val route: String) {
    object Launcher : Screen("launcher")
    object BpInput : Screen("bp_input")
    object Login : Screen("login/{bpNumber}")   // for future login screen
    object Signup : Screen("signup/{bpNumber}") // FIXED
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")               // for home screen
    object SendNotification : Screen("send_notification")
    object NotificationHistory : Screen("notification_history")
    object Profile : Screen("profile")
    object AdminPanel : Screen("admin_panel")
}