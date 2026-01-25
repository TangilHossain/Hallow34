package com.shawonshagor0.hallow34

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.shawonshagor0.hallow34.data.remote.HallowFirebaseMessagingService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HallowApp : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)

            // Subscribe to FCM topic
            HallowFirebaseMessagingService.subscribeToAllUsers()
        } catch (e: Exception) {
            Log.e("HallowApp", "Initialization error: ${e.message}", e)
        }
    }
}
