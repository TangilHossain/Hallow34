package com.shawonshagor0.hallow34.presentation.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shawonshagor0.hallow34.data.local.SessionManager
import com.shawonshagor0.hallow34.domain.model.User
import com.shawonshagor0.hallow34.domain.usecase.GetAllUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var selectedDistrict by mutableStateOf("")
        private set

    // List of all Bangladesh districts
    val districts = listOf(
        "All Districts",
        "Dhaka", "Faridpur", "Gazipur", "Gopalganj", "Kishoreganj", "Madaripur",
        "Manikganj", "Munshiganj", "Narayanganj", "Narsingdi", "Rajbari",
        "Shariatpur", "Tangail",
        "Chattogram", "Bandarban", "Brahmanbaria", "Chandpur", "Cumilla", "Cox's Bazar",
        "Feni", "Khagrachhari", "Lakshmipur", "Noakhali", "Rangamati",
        "Rajshahi", "Bogura", "Chapainawabganj", "Joypurhat", "Naogaon",
        "Natore", "Pabna", "Sirajganj",
        "Khulna", "Bagerhat", "Chuadanga", "Jashore", "Jhenaidah",
        "Kushtia", "Magura", "Meherpur", "Narail", "Satkhira",
        "Barishal", "Barguna", "Bhola", "Jhalokathi",
        "Patuakhali", "Pirojpur",
        "Sylhet", "Habiganj", "Moulvibazar", "Sunamganj",
        "Rangpur", "Dinajpur", "Gaibandha", "Kurigram",
        "Lalmonirhat", "Nilphamari", "Panchagarh", "Thakurgaon",
        "Mymensingh", "Jamalpur", "Netrokona", "Sherpur"
    )

    fun onDistrictFilterChange(district: String) {
        selectedDistrict = if (district == "All Districts") "" else district
    }

    // Current user's BP number
    val currentBpNumber: String?
        get() = sessionManager.getSavedBpNumber()

    // Check if current user is admin
    val isAdmin: Boolean
        get() = currentBpNumber == "12345678"

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            getAllUsersUseCase()
                .catch { /* Handle error if needed */ }
                .collect { userList ->
                    users = userList
                    isRefreshing = false
                }
        }
    }

    fun refreshUsers() {
        isRefreshing = true
        loadUsers()
    }

    fun onSearchChange(query: String) {
        searchQuery = query
    }

    val filteredUsers: List<User>
        get() {
            var result = users

            // Apply district filter first
            if (selectedDistrict.isNotBlank()) {
                result = result.filter { user ->
                    user.district.equals(selectedDistrict, ignoreCase = true)
                }
            }

            // Then apply search query filter
            if (searchQuery.isNotBlank()) {
                result = result.filter { user ->
                    listOf(
                        user.fullName,
                        user.designation,
                        user.phone,
                        user.bloodGroup,
                        user.email,
                        user.district,
                        user.postingPlace,
                        user.facebookProfileLink
                    ).any { field ->
                        field.contains(searchQuery, ignoreCase = true)
                    }
                }
            }

            return result
        }
}
