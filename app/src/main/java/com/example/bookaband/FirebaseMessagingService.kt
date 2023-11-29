package com.example.bookaband

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// Set up FirebaseMessagingService to handle incoming messages

const val  channelId="notification_channel"
const val channelName="com.example.bookaband"
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage){
        if(remoteMessage.getNotification() != null){
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
        }
    }

    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView =RemoteViews("com.example.bookaband",R.layout.notification_item)

        remoteView.setTextViewText(R.id.titleN,title)
        remoteView.setTextViewText(R.id.messageN,message)
        remoteView.setImageViewResource(R.id.imageView,R.drawable.icon_only)

        return remoteView

    }

fun generateNotification(title: String, message: String){
    val intent=Intent(this, Landing::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    val pendingIntent = PendingIntent.getActivities(this, 0 , arrayOf(intent),
        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
    var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
        .setSmallIcon(R.drawable.icon_only)
        .setAutoCancel(true)
        .setVibrate(longArrayOf(1000,1000,1000,1000))
        .setOnlyAlertOnce(true)
        .setContentIntent(pendingIntent)

    builder = builder.setContent(getRemoteView(title,message))

    val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)

        notificationManager.notify(0,builder.build())

    }

    }


}




//var notificationCount = 0
//
//override fun onMessageReceived(remoteMessage: RemoteMessage) {
//    // Handle incoming FCM messages here
//    // Extract notification content from remoteMessage
//    val title = remoteMessage.notification?.title
//    val message = remoteMessage.notification?.body
//
//    // Display notification to the user
//    // You can use NotificationCompat.Builder to create a notification
//    // Create a notification channel
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val channelId = "channel_id"
//        val channelName = "Channel Name"
//        val importance = NotificationManager.IMPORTANCE_DEFAULT
//        val channel = NotificationChannel(channelId, channelName, importance)
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//    }
//    // Increment the notificationCount for each new notification
//    notificationCount++
//
//// Use notificationCount as the notificationId
//    val notificationId = notificationCount
//
//    // Build the notification using NotificationCompat.Builder
//    val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
//        .setContentTitle(title)
//        .setContentText(message)
//        .setSmallIcon(R.drawable.icon_only) // Replace with your icon
//        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//// Create an explicit intent for when the notification is tapped
//    val intent = Intent(this, Landing::class.java)
//    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//    notificationBuilder.setContentIntent(pendingIntent)
//
//// Display the notification using NotificationManager
//    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    notificationManager.notify(notificationId, notificationBuilder.build())
//
//
//    // and display it using NotificationManager
//}
//
//override fun onNewToken(token: String) {
//    // If the FCM token is refreshed, update it on your server
//    // This token uniquely identifies the app instance on a device
//    // Send this token to your server and associate it with the user
//}
