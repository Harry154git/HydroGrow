package com.pemrogamanmobile.hydrogrow

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pemrogamanmobile.hydrogrow.util.NotificationHelper
import com.pemrogamanmobile.hydrogrow.worker.PlantCheckWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class HydroGrowApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        schedulePlantCheck()
        NotificationHelper.createNotificationChannel(this)
    }

    private fun schedulePlantCheck() {
        val plantCheckWorkRequest = PeriodicWorkRequestBuilder<PlantCheckWorker>(24, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PlantCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,
            plantCheckWorkRequest
        )
    }
}