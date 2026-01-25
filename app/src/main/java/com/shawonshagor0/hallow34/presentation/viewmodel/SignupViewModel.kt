package com.shawonshagor0.hallow34.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shawonshagor0.hallow34.domain.model.User
import com.shawonshagor0.hallow34.domain.usecase.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val saveUserUseCase: SaveUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SignupState>(SignupState.Idle)
    val state: StateFlow<SignupState> = _state

    fun signupUser(
        bpNumber: String,
        fullName: String,
        designation: String,
        district: String,
        currentRange: String,
        bloodGroup: String,
        phone: String,
        email: String,
        profileImageUri: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _state.value = SignupState.Loading

            try {
                // Upload image to Cloudinary if exists
                val profileUrl = profileImageUri?.let { uri ->
                    uploadToCloudinary(uri)
                } ?: ""

                // Create User model
                val user = User(
                    bpNumber = bpNumber,
                    fullName = fullName,
                    designation = designation,
                    district = district,
                    currentRange = currentRange,
                    bloodGroup = bloodGroup,
                    phone = phone,
                    email = email,
                    imageUrl = profileUrl
                )

                // Save to Firestore
                saveUserUseCase(user)

                _state.value = SignupState.Success
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = SignupState.Error(e.localizedMessage ?: "Error")
            }
        }
    }

    // Placeholder for image upload to Cloudinary
    private suspend fun uploadToCloudinary(uri: Uri): String {
        // Implement Cloudinary upload here
        // Return the URL of uploaded image
        // For now, returning dummy URL
        return "https://res.cloudinary.com/demo/image/upload/${UUID.randomUUID()}.jpg"
    }

    // Optional helper: convert Bitmap to Uri (for camera images)
    fun saveBitmapAsUri(bitmap: Bitmap): Uri {
        val file = File.createTempFile("temp_image", ".jpg")
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        return Uri.fromFile(file)
    }
}

// Signup UI state
sealed class SignupState {
    object Idle : SignupState()
    object Loading : SignupState()
    object Success : SignupState()
    data class Error(val message: String) : SignupState()
}
