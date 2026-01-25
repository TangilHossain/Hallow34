package com.shawonshagor0.hallow34.data.remote

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.shawonshagor0.hallow34.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class CloudinaryUploader @Inject constructor() {

    companion object {
        // Credentials loaded from cloudinary.properties via BuildConfig
        private val CLOUD_NAME = BuildConfig.CLOUDINARY_CLOUD_NAME
        private val API_KEY = BuildConfig.CLOUDINARY_API_KEY
        private val API_SECRET = BuildConfig.CLOUDINARY_API_SECRET
        private val UPLOAD_PRESET = BuildConfig.CLOUDINARY_UPLOAD_PRESET

        private var isInitialized = false

        fun initialize(context: Context) {
            if (!isInitialized) {
                val config = mapOf(
                    "cloud_name" to CLOUD_NAME,
                    "api_key" to API_KEY,
                    "api_secret" to API_SECRET,
                    "secure" to true
                )
                MediaManager.init(context, config)
                isInitialized = true
            }
        }
    }

    suspend fun uploadImage(context: Context, imageUri: Uri): String {
        // Ensure initialized
        initialize(context)

        return suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(imageUri)
                .unsigned(UPLOAD_PRESET)
                .option("folder", "hallow34_profiles")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        // Upload started
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        // Progress update
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"] as? String
                        if (secureUrl != null) {
                            continuation.resume(secureUrl)
                        } else {
                            continuation.resumeWithException(Exception("Failed to get image URL"))
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resumeWithException(Exception(error.description))
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        // Rescheduled
                    }
                })
                .dispatch()
        }
    }
}
