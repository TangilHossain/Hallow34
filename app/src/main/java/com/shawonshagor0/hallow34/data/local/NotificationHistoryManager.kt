package com.shawonshagor0.hallow34.data.local

import android.content.Context
import android.content.SharedPreferences
import com.shawonshagor0.hallow34.data.model.NotificationHistory
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHistoryManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREF_NAME = "hallow34_notification_history"
        private const val KEY_NOTIFICATIONS = "notifications"
        private const val MAX_NOTIFICATIONS = 100 // Keep last 100 notifications
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Save a notification to history
     */
    fun saveNotification(title: String, message: String, isSent: Boolean) {
        val notification = NotificationHistory(
            id = System.currentTimeMillis().toString(),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            isSent = isSent
        )

        val notifications = getNotifications().toMutableList()
        notifications.add(0, notification) // Add to beginning

        // Keep only the last MAX_NOTIFICATIONS
        val trimmedNotifications = notifications.take(MAX_NOTIFICATIONS)

        saveNotificationsToPrefs(trimmedNotifications)
    }

    /**
     * Get all notifications from history
     */
    fun getNotifications(): List<NotificationHistory> {
        val jsonString = prefs.getString(KEY_NOTIFICATIONS, null) ?: return emptyList()

        return try {
            val jsonArray = JSONArray(jsonString)
            val notifications = mutableListOf<NotificationHistory>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                notifications.add(
                    NotificationHistory(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        message = obj.getString("message"),
                        timestamp = obj.getLong("timestamp"),
                        isSent = obj.getBoolean("isSent")
                    )
                )
            }
            notifications
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Clear all notification history
     */
    fun clearHistory() {
        prefs.edit().remove(KEY_NOTIFICATIONS).apply()
    }

    private fun saveNotificationsToPrefs(notifications: List<NotificationHistory>) {
        val jsonArray = JSONArray()

        notifications.forEach { notification ->
            val obj = JSONObject().apply {
                put("id", notification.id)
                put("title", notification.title)
                put("message", notification.message)
                put("timestamp", notification.timestamp)
                put("isSent", notification.isSent)
            }
            jsonArray.put(obj)
        }

        prefs.edit().putString(KEY_NOTIFICATIONS, jsonArray.toString()).apply()
    }
}
