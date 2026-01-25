package com.shawonshagor0.hallow34.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.shawonshagor0.hallow34.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    /**
     * Check if user should be auto-logged in
     */
    fun checkAutoLogin(): Boolean {
        return sessionManager.isLoggedIn()
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
        sessionManager.clearSession()
        onComplete()
    }
}
