package com.shawonshagor0.hallow34.data.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shawonshagor0.hallow34.MainActivity
import com.shawonshagor0.hallow34.R
import com.shawonshagor0.hallow34.data.local.NotificationHistoryManager

class HallowFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationHistoryManager by lazy {
        NotificationHistoryManager(applicationContext)
    }

    companion object {
        private const val CHANNEL_ID = "hallow34_notifications"
        private const val CHANNEL_NAME = "Hallow34 Notifications"

        /**
         * Call this to subscribe the user to receive all notifications
         */
        fun subscribeToAllUsers() {
            FirebaseMessaging.getInstance().subscribeToTopic(NotificationSender.ALL_USERS_TOPIC)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("Subscribed to all_users topic")
                    } else {
                        println("Failed to subscribe to topic: ${task.exception?.message}")
                    }
                }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Optionally save the token to Firestore for the user
        println("FCM Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Get notification data
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Hallow34"

        val message = remoteMessage.notification?.body
            ?: remoteMessage.data["message"]
            ?: ""

        // Save to notification history
        notificationHistoryManager.saveNotification(title, message, isSent = false)

        showNotification(title, message)
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications from Hallow34 app"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open app when notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Show notification with unique ID
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
