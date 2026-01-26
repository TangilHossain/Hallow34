package com.shawonshagor0.hallow34.data.model

data class NotificationHistory(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isSent: Boolean // true if sent by user, false if received
)
