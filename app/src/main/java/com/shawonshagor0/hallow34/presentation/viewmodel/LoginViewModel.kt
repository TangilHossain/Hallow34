package com.shawonshagor0.hallow34.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shawonshagor0.hallow34.data.auth.FirebaseAuthManager
import com.shawonshagor0.hallow34.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val firebaseAuthManager: FirebaseAuthManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    /**
     * Check if user should be auto-logged in
     * Now checks both Firebase Auth and session manager
     */
    fun checkAutoLogin(): Boolean {
        // Check if Firebase Auth has a current user AND remember me is enabled
        return firebaseAuthManager.isSignedIn() && sessionManager.isLoggedIn()
    }

    /**
     * Sign in user with BP number and password using Firebase Auth
     */
    fun signIn(bpNumber: String, password: String, rememberMe: Boolean, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val result = firebaseAuthManager.signIn(bpNumber, password)

            result.fold(
                onSuccess = {
                    sessionManager.saveSession(bpNumber, rememberMe)
                    _loginState.value = LoginState.Success
                    onSuccess()
                },
                onFailure = { e ->
                    _loginState.value = LoginState.Error(e.message ?: "Login failed")
                    onError(e.message ?: "Login failed")
                }
            )
        }
    }

    /**
     * Save session when user logs in with "Remember Me"
     */
    fun saveSession(bpNumber: String, rememberMe: Boolean, onComplete: () -> Unit) {
        sessionManager.saveSession(bpNumber, rememberMe)
        onComplete()
    }

    /**
     * Clear session on logout
     */
    fun logout(onComplete: () -> Unit) {
        firebaseAuthManager.signOut()
        sessionManager.clearSession()
        onComplete()
    }

    /**
     * Get current BP number
     */
    fun getCurrentBpNumber(): String? {
        return firebaseAuthManager.getCurrentBpNumber() ?: sessionManager.getSavedBpNumber()
    }
}

// Login UI state
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
