package com.shawonshagor0.hallow34.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthManager @Inject constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        // Convert BP number to fake email format for Firebase Auth
        fun bpNumberToEmail(bpNumber: String): String {
            return "${bpNumber}@user.com"
        }

        // Extract BP number from fake email
        fun emailToBpNumber(email: String): String {
            return email.replace("@user.com", "")
        }
    }

    /**
     * Get current Firebase user
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    /**
     * Check if user is currently signed in with Firebase
     */
    fun isSignedIn(): Boolean = auth.currentUser != null

    /**
     * Get current user's BP number
     */
    fun getCurrentBpNumber(): String? {
        return auth.currentUser?.email?.let { emailToBpNumber(it) }
    }

    /**
     * Sign up a new user with BP number and password
     */
    suspend fun signUp(bpNumber: String, password: String): Result<FirebaseUser> {
        return try {
            val email = bpNumberToEmail(bpNumber)
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Sign up failed: User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign in existing user with BP number and password
     */
    suspend fun signIn(bpNumber: String, password: String): Result<FirebaseUser> {
        return try {
            val email = bpNumberToEmail(bpNumber)
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Sign in failed: User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign out the current user
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(bpNumber: String): Result<Unit> {
        return try {
            val email = bpNumberToEmail(bpNumber)
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
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
