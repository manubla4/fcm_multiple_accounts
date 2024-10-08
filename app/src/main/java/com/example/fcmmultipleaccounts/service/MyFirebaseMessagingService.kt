package com.example.fcmmultipleaccounts.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.fcmmultipleaccounts.MainActivity
import com.example.fcmmultipleaccounts.R
import com.example.fcmmultipleaccounts.utils.FIREBASE_SECOND_INSTANCE
import com.example.fcmmultipleaccounts.utils.FIREBASE_THIRD_INSTANCE
import com.example.fcmmultipleaccounts.utils.instancesMap
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "From Instance: ${instancesMap[remoteMessage.from] ?: ""}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it)
        }

    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.result == token) {
                Log.d(TAG, "Refreshed token belongs to default instance")
            }
            else {
                val secondInstanceApp = FirebaseApp.getInstance(FIREBASE_SECOND_INSTANCE)
                val secondFcm = secondInstanceApp.get(FirebaseMessaging::class.java)
                secondFcm?.token?.addOnCompleteListener {
                    if (it.result == token) {
                        Log.d(TAG, "Refreshed token belongs to second instance")
                    }
                    else {
                        val thirdInstanceApp = FirebaseApp.getInstance(FIREBASE_THIRD_INSTANCE)
                        val thirdFcm = thirdInstanceApp.get(FirebaseMessaging::class.java)
                        thirdFcm?.token?.addOnCompleteListener {
                            if (it.result == token) {
                                Log.d(TAG, "Refreshed token belongs to third instance")
                            }
                        }
                    }
                }
            }
        }
    }


    private fun sendNotification(notif: RemoteMessage.Notification) {
        val requestCode = 0
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(notif.title ?: "Default Title")
            .setContentText(notif.body ?: "Default Body")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setColor(resources.getColor(R.color.colorAccent))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Custom channel title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = (1..1000000).random()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}