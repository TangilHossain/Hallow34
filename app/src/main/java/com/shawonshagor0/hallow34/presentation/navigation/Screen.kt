package com.shawonshagor0.hallow34.presentation.navigation

sealed class Screen(val route: String) {
    object Launcher : Screen("launcher")
    object BpInput : Screen("bp_input")
    object Login : Screen("login")
    object Signup : Screen("signup/{bpNumber}")
    object Home : Screen("home")
}