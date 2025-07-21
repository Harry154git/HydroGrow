package com.pemrogamanmobile.hydrogrow

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pemrogamanmobile.hydrogrow.util.NotificationHelper
import com.pemrogamanmobile.hydrogrow.worker.PlantCheckWorker
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class HydroGrowApp : Application() {
    override fun onCreate() {
        super.onCreate()
        schedulePlantCheck()
        NotificationHelper.createNotificationChannel(this)
    }

    private fun schedulePlantCheck() {
        // Membuat request kerja yang akan berulang setiap 24 jam
        val plantCheckWorkRequest = PeriodicWorkRequestBuilder<PlantCheckWorker>(24, TimeUnit.HOURS)
            .build()

        // Menjadwalkan pekerjaan dengan nama unik
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PlantCheckWork", // Nama unik untuk pekerjaan ini
            ExistingPeriodicWorkPolicy.KEEP, // Jika sudah ada, jangan buat yang baru
            plantCheckWorkRequest
        )
    }
}


