package com.shawonshagor0.hallow34.data.remote

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.shawonshagor0.hallow34.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationSender @Inject constructor() {

    companion object {
        // FCM HTTP v1 API endpoint
        private const val FCM_SCOPE = "https://www.googleapis.com/auth/firebase.messaging"

        // Your Firebase project ID (from google-services.json)
        private const val PROJECT_ID = "hallow34-e938d"

        // Topic that all users subscribe to
        const val ALL_USERS_TOPIC = "all_users"
    }

    /**
     * Send push notification to all users subscribed to the "all_users" topic
     * Uses FCM HTTP v1 API with service account authentication
     */
    suspend fun sendToAllUsers(context: Context, title: String, message: String) {
        withContext(Dispatchers.IO) {
            // Get access token from service account
            val accessToken = getAccessToken(context)

            val url = URL("https://fcm.googleapis.com/v1/projects/$PROJECT_ID/messages:send")
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; UTF-8")
                connection.setRequestProperty("Authorization", "Bearer $accessToken")
                connection.doOutput = true

                // Create notification payload for FCM v1 API
                val payload = JSONObject().apply {
                    put("message", JSONObject().apply {
                        put("topic", ALL_USERS_TOPIC)
                        put("notification", JSONObject().apply {
                            put("title", title)
                            put("body", message)
                        })
                        put("android", JSONObject().apply {
                            put("priority", "high")
                            put("notification", JSONObject().apply {
                                put("channel_id", "hallow34_notifications")
                                put("sound", "default")
                                put("default_vibrate_timings", true)
                                put("default_light_settings", true)
                                put("notification_priority", "PRIORITY_HIGH")
                                put("visibility", "PUBLIC")
                            })
                        })
                        put("data", JSONObject().apply {
                            put("title", title)
                            put("message", message)
                        })
                    })
                }

                connection.outputStream.use { os ->
                    os.write(payload.toString().toByteArray(Charsets.UTF_8))
                    os.flush()
                }

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    val errorStream = connection.errorStream?.bufferedReader()?.readText()
                    throw Exception("FCM Error: $responseCode - $errorStream")
                }

            } finally {
                connection.disconnect()
            }
        }
    }

    /**
     * Get OAuth2 access token using service account credentials
     */
    private fun getAccessToken(context: Context): String {
        val serviceAccountStream = context.resources.openRawResource(R.raw.service_account)
        val credentials = GoogleCredentials
            .fromStream(serviceAccountStream)
            .createScoped(listOf(FCM_SCOPE))

        credentials.refreshIfExpired()
        return credentials.accessToken.tokenValue
    }
}
