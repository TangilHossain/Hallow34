package com.shawonshagor0.hallow34.presentation.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.shawonshagor0.hallow34.data.model.User
import com.shawonshagor0.hallow34.data.repository.UserRepository

class HomeViewModel : ViewModel() {

    private val repository = UserRepository()

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    init {
        loadUsers()
    }

    private fun loadUsers() {
        repository.getAllUsers {
            users = it
        }
    }

    fun onSearchChange(query: String) {
        searchQuery = query
    }

    val filteredUsers: List<User>
        get() {
            if (searchQuery.isBlank()) return users

            return users.filter { user ->
                listOf(
                    user.fullName,
                    user.designation,
                    user.phone,
                    user.bloodGroup,
                    user.email,
                    user.district,
                    user.range
                ).any { field ->
                    field.contains(searchQuery, ignoreCase = true)
                }
            }
        }
}
