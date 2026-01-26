package com.shawonshagor0.hallow34.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREF_NAME = "hallow34_session"
        private const val KEY_BP_NUMBER = "bp_number"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Save user session when "Remember Me" is checked
     */
    fun saveSession(bpNumber: String, rememberMe: Boolean) {
        prefs.edit().apply {
            putString(KEY_BP_NUMBER, bpNumber) // Always save BP number
            if (rememberMe) {
                putBoolean(KEY_REMEMBER_ME, true)
                putBoolean(KEY_IS_LOGGED_IN, true)
            } else {
                putBoolean(KEY_IS_LOGGED_IN, true)
                putBoolean(KEY_REMEMBER_ME, false)
            }
            apply()
        }
    }

    /**
     * Check if user should be auto-logged in
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) &&
               prefs.getBoolean(KEY_REMEMBER_ME, false)
    }

    /**
     * Get saved BP number (always available when logged in)
     */
    fun getSavedBpNumber(): String? {
        return prefs.getString(KEY_BP_NUMBER, null)
    }

    /**
     * Get BP number only if remember me was checked (for auto-login)
     */
    fun getAutoLoginBpNumber(): String? {
        return if (prefs.getBoolean(KEY_REMEMBER_ME, false)) {
            prefs.getString(KEY_BP_NUMBER, null)
        } else null
    }

    /**
     * Clear session on logout
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
