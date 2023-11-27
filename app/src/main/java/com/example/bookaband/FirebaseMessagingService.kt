package com.example.bookaband

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.notification?.let { notification ->
            val title = notification.title
            val body = notification.body

            // Perform actions based on the received message
            // For example, display a notification or trigger some action
            // ...

            // You can use a notification manager or any other method to show the notification
            // ...
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
