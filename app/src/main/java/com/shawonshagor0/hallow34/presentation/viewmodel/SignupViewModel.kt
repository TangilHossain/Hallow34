package com.shawonshagor0.hallow34.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shawonshagor0.hallow34.data.auth.FirebaseAuthManager
import com.shawonshagor0.hallow34.data.local.SessionManager
import com.shawonshagor0.hallow34.data.remote.CloudinaryUploader
import com.shawonshagor0.hallow34.domain.model.User
import com.shawonshagor0.hallow34.domain.usecase.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val saveUserUseCase: SaveUserUseCase,
    private val cloudinaryUploader: CloudinaryUploader,
    private val sessionManager: SessionManager,
    private val firebaseAuthManager: FirebaseAuthManager
) : ViewModel() {

    private val _state = MutableStateFlow<SignupState>(SignupState.Idle)
    val state: StateFlow<SignupState> = _state

    fun signupUser(
        context: Context,
        bpNumber: String,
        fullName: String,
        designation: String,
        district: String,
        postingPlace: String,
        bloodGroup: String,
        phone: String,
        email: String,
        password: String,
        facebookProfileLink: String,
        profileImageUri: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _state.value = SignupState.Loading

            try {
                // 1. Create Firebase Auth account with real email
                val authResult = firebaseAuthManager.signUp(email, password)

                authResult.fold(
                    onSuccess = { firebaseUser ->
                        try {
                            // 2. Upload image to Cloudinary if exists
                            val profileUrl = profileImageUri?.let { uri ->
                                cloudinaryUploader.uploadImage(context, uri)
                            } ?: ""

                            // 3. Create User model (don't store password in Firestore anymore)
                            val user = User(
                                bpNumber = bpNumber,
                                fullName = fullName,
                                designation = designation,
                                district = district,
                                postingPlace = postingPlace,
                                bloodGroup = bloodGroup,
                                phone = phone,
                                email = email,
                                password = "", // Don't store password in Firestore
                                facebookProfileLink = facebookProfileLink,
                                imageUrl = profileUrl
                            )

                            // 4. Save to Firestore
                            saveUserUseCase(user)

                            // 5. Save session (auto-remember for new signups)
                            sessionManager.saveSession(bpNumber, rememberMe = true)

                            _state.value = SignupState.Success
                            onSuccess()
                        } catch (e: Exception) {
                            // If Firestore save fails, delete the Firebase Auth account
                            firebaseAuthManager.deleteAccount()
                            e.printStackTrace()
                            _state.value = SignupState.Error(e.localizedMessage ?: "Error saving user data")
                        }
                    },
                    onFailure = { e ->
                        e.printStackTrace()
                        _state.value = SignupState.Error(
                            when {
                                e.message?.contains("email address is already in use") == true ->
                                    "This email address is already registered"
                                e.message?.contains("password is invalid") == true ->
                                    "Password must be at least 6 characters"
                                e.message?.contains("email address is badly formatted") == true ->
                                    "Please enter a valid email address"
                                else -> e.localizedMessage ?: "Sign up failed"
                            }
                        )
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = SignupState.Error(e.localizedMessage ?: "Error")
            }
        }
    }

    // Optional helper: convert Bitmap to Uri (for camera images)
    fun saveBitmapAsUri(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
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
