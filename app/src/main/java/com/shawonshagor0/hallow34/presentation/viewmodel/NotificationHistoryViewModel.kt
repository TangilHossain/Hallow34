package com.shawonshagor0.hallow34.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.shawonshagor0.hallow34.data.local.NotificationHistoryManager
import com.shawonshagor0.hallow34.data.model.NotificationHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationHistoryViewModel @Inject constructor(
    private val notificationHistoryManager: NotificationHistoryManager
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationHistory>>(emptyList())
    val notifications: StateFlow<List<NotificationHistory>> = _notifications

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        _notifications.value = notificationHistoryManager.getNotifications()
    }

    fun clearHistory() {
        notificationHistoryManager.clearHistory()
        _notifications.value = emptyList()
    }
}
