package com.shawonshagor0.hallow34.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthManager @Inject constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Get current Firebase user
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    /**
     * Check if user is currently signed in with Firebase
     */
    fun isSignedIn(): Boolean = auth.currentUser != null

    /**
     * Sign up a new user with real email and password
     */
    suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Sign up failed: User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign in existing user with email and password
     */
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Sign in failed: User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign in existing user with BP number and password
     * Looks up the email from Firestore first, then signs in with Firebase Auth
     */
    suspend fun signIn(bpNumber: String, password: String): Result<FirebaseUser> {
        return try {
            // First, look up the user's email from Firestore
            val doc = firestore.collection("users").document(bpNumber).get().await()

            if (!doc.exists()) {
                return Result.failure(Exception("User not found"))
            }

            val email = doc.getString("email")
            if (email.isNullOrBlank()) {
                return Result.failure(Exception("No email found for this user"))
            }

            // Sign in with the real email
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Sign in failed: User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get user's email from Firestore by BP number
     */
    suspend fun getEmailByBpNumber(bpNumber: String): String? {
        return try {
            val doc = firestore.collection("users").document(bpNumber).get().await()
            if (doc.exists()) {
                doc.getString("email")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Sign out the current user
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Send password reset email using the real email stored in Firestore
     * Note: This only works for users who registered with their real email (after migration)
     * For users who registered with bpNumber@user.com, they need to re-register
     */
    suspend fun sendPasswordResetEmail(bpNumber: String): Result<Unit> {
        return try {
            // Look up the real email from Firestore
            val email = getEmailByBpNumber(bpNumber)
            if (email.isNullOrBlank()) {
                return Result.failure(Exception("No email found for this BP number"))
            }

            // Check if email looks like the old fake format
            if (email.endsWith("@user.com")) {
                return Result.failure(Exception("This account uses the old format. Please contact support to reset your password."))
            }

            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            // Provide more specific error messages
            val errorMessage = when {
                e.message?.contains("no user record") == true ||
                e.message?.contains("user-not-found") == true ->
                    "No Firebase account found with this email. You may need to re-register."
                e.message?.contains("invalid-email") == true ->
                    "Invalid email address format"
                else -> e.message ?: "Failed to send reset email"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Update password for current user
     */
    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            auth.currentUser?.updatePassword(newPassword)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete current user's Firebase Auth account
     */
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            auth.currentUser?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
