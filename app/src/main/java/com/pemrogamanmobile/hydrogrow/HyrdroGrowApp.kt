package com.pemrogamanmobile.hydrogrow

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory // <-- Import ini
import androidx.work.Configuration // <-- Import ini
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pemrogamanmobile.hydrogrow.util.NotificationHelper
import com.pemrogamanmobile.hydrogrow.worker.PlantCheckWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject // <-- Import ini

@HiltAndroidApp
// Langkah 1: Implementasikan Configuration.Provider
class HydroGrowApp : Application(), Configuration.Provider {

    // Langkah 2: Inject HiltWorkerFactory
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // Langkah 3: Override konfigurasi WorkManager untuk menyediakan factory
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Bagian ini sudah benar, tidak perlu diubah
        schedulePlantCheck()
        NotificationHelper.createNotificationChannel(this)
    }

    private fun schedulePlantCheck() {
        // Bagian ini juga sudah benar, tidak perlu diubah
        val plantCheckWorkRequest = PeriodicWorkRequestBuilder<PlantCheckWorker>(24, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PlantCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,
            plantCheckWorkRequest
        )
    }
}