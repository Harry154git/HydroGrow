package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pemrogamanmobile.hydrogrow.R
import com.pemrogamanmobile.hydrogrow.presentation.MainActivity // Ganti dengan activity tujuan Anda
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle notifikasi saat aplikasi di foreground atau background
        remoteMessage.notification?.let { notification ->
            val title = notification.title
            val body = notification.body
            val postId = remoteMessage.data["postId"]

            if (title != null && body != null) {
                showNotification(title, body, postId)
            }
        }
    }

    // Panggil ini saat user login atau saat token di-refresh
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Kirim token ini ke server/Firestore Anda untuk disimpan di dokumen user
        // Misalnya: sendTokenToServer(token)
    }

    private fun showNotification(title: String, message: String, postId: String?) {
        val channelId = "hydrogrow_channel"
        val channelName = "HydroGrow Notifications"

        // Intent untuk membuka aplikasi saat notifikasi diklik
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("POST_ID_FROM_NOTIFICATION", postId) // Kirim ID postingan ke activity
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_logo) // Ganti dengan ikon notifikasi Anda
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat Notification Channel untuk Android Oreo (API 26) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Tampilkan notifikasi dengan ID unik
        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }
}