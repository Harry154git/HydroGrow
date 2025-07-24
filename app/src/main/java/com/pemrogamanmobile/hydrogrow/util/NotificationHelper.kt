package com.pemrogamanmobile.hydrogrow.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context // <-- PASTIKAN IMPORT INI YANG DIGUNAKAN
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.pemrogamanmobile.hydrogrow.R
import com.pemrogamanmobile.hydrogrow.presentation.MainActivity
import android.content.pm.PackageManager // <-- Tambahkan import ini
import androidx.core.content.ContextCompat // <-- Tambahkan import ini

object NotificationHelper {

    private const val CHANNEL_ID = "hydrogrow_channel_id"
    private const val CHANNEL_NAME = "HydroGrow Notifikasi Panen"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel untuk notifikasi panen tanaman HydroGrow."
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    suspend fun sendHarvestNotification(
        context: Context, // <-- PERBAIKAN: Diubah dari CoroutineContext menjadi Context
        id: String,
        plantName: String,
        imageUrl: String?,
        message: String,
        notificationId: Int
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Jika izin BELUM diberikan, jangan lanjutkan fungsi ini.
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Kita bisa menambahkan log di sini jika perlu, tapi intinya kita berhenti.
                // Peringatan: Jangan meminta izin dari sini karena ini bukan UI context.
                return
            }
        }

        // Buat Intent yang akan terbuka saat notifikasi diklik
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Kirim data tambahan ke activity jika diperlukan
            putExtra("PLANT_ID_FROM_NOTIFICATION", id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId, // Gunakan notificationId yang unik untuk setiap PendingIntent
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Muat gambar tanaman dari URL menggunakan Coil
        // 'context' di sini sekarang sudah benar merujuk ke Android Context
        val largeIconBitmap = loadBitmapFromUrl(context, imageUrl)

        // Semua pemanggilan 'context' di bawah ini sekarang sudah valid.
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle("Waktunya Panen!")
            .setContentText(message)
            .setLargeIcon(largeIconBitmap)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    private suspend fun loadBitmapFromUrl(context: Context, url: String?): Bitmap? {
        if (url.isNullOrEmpty()) return null

        return try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            (result as BitmapDrawable).bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}