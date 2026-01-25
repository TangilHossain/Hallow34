package com.shawonshagor0.hallow34.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shawonshagor0.hallow34.data.remote.NotificationSender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationSender: NotificationSender
) : ViewModel() {

    private val _state = MutableStateFlow<NotificationState>(NotificationState.Idle)
    val state: StateFlow<NotificationState> = _state

    fun sendNotificationToAll(context: Context, title: String, message: String) {
        viewModelScope.launch {
            _state.value = NotificationState.Loading

            try {
                notificationSender.sendToAllUsers(context, title, message)
                _state.value = NotificationState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = NotificationState.Error(e.localizedMessage ?: "Failed to send notification")
            }
        }
    }
}

sealed class NotificationState {
    object Idle : NotificationState()
    object Loading : NotificationState()
    object Success : NotificationState()
    data class Error(val message: String) : NotificationState()
}
