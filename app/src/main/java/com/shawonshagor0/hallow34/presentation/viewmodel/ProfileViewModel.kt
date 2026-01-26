package com.shawonshagor0.hallow34.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shawonshagor0.hallow34.data.local.SessionManager
import com.shawonshagor0.hallow34.data.remote.CloudinaryUploader
import com.shawonshagor0.hallow34.domain.model.User
import com.shawonshagor0.hallow34.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val cloudinaryUploader: CloudinaryUploader,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            try {
                val bpNumber = sessionManager.getSavedBpNumber()
                if (bpNumber != null) {
                    val user = userRepository.getUserByBpNumber(bpNumber)
                    if (user != null) {
                        _user.value = user
                        _state.value = ProfileState.Success
                    } else {
                        _state.value = ProfileState.Error("User not found")
                    }
                } else {
                    _state.value = ProfileState.Error("Not logged in")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = ProfileState.Error(e.localizedMessage ?: "Failed to load profile")
            }
        }
    }

    fun updateProfile(
        context: Context,
        fullName: String,
        designation: String,
        district: String,
        currentRange: String,
        bloodGroup: String,
        phone: String,
        email: String,
        newImageUri: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _state.value = ProfileState.Updating
            try {
                val currentUser = _user.value ?: return@launch

                // Upload new image if provided
                val imageUrl = if (newImageUri != null) {
                    cloudinaryUploader.uploadImage(context, newImageUri)
                } else {
                    currentUser.imageUrl
                }

                val updatedUser = currentUser.copy(
                    fullName = fullName,
                    designation = designation,
                    district = district,
                    currentRange = currentRange,
                    bloodGroup = bloodGroup,
                    phone = phone,
                    email = email,
                    imageUrl = imageUrl
                )

                userRepository.saveUser(updatedUser)
                _user.value = updatedUser
                _state.value = ProfileState.Success
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = ProfileState.Error(e.localizedMessage ?: "Failed to update profile")
            }
        }
    }

    fun saveBitmapAsUri(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "temp_profile_${System.currentTimeMillis()}.jpg")
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        return Uri.fromFile(file)
    }
}

sealed class ProfileState {
    object Loading : ProfileState()
    object Success : ProfileState()
    object Updating : ProfileState()
    data class Error(val message: String) : ProfileState()
}
