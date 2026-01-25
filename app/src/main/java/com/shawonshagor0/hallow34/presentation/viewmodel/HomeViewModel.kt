package com.shawonshagor0.hallow34.presentation.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shawonshagor0.hallow34.domain.model.User
import com.shawonshagor0.hallow34.domain.usecase.GetAllUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase
) : ViewModel() {

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            getAllUsersUseCase()
                .catch { /* Handle error if needed */ }
                .collect { userList ->
                    users = userList
                }
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
                    user.currentRange
                ).any { field ->
                    field.contains(searchQuery, ignoreCase = true)
                }
            }
        }
}
