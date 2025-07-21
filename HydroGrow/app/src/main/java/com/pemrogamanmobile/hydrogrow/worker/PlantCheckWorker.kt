package com.pemrogamanmobile.hydrogrow.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pemrogamanmobile.hydrogrow.domain.repository.PlantRepository // Ganti dengan path yang benar
import com.pemrogamanmobile.hydrogrow.domain.usecase.plant.PlantUseCase // Ganti dengan path yang benar

class PlantCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    // Dapatkan instance use case, biasanya melalui Dependency Injection (Hilt/Koin)
    private val plantUseCase: PlantUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            // 1. Dapatkan semua tanaman dari semua kebun
            //    (Logika ini mungkin perlu disesuaikan. Asumsinya kita cek semua tanaman milik user)
            val allPlants = plantUseCase.getAllPlants() // Anda mungkin perlu menambah fungsi ini di repository/usecase

            // 2. Loop setiap tanaman dan jalankan logika countdown
            allPlants.forEach { plant ->
                // PERHATIAN: Worker di background tidak bisa menampilkan dialog konfirmasi UI.
                // Jadi, kita modifikasi logikanya. Worker hanya akan mengirim notifikasi.
                // Konfirmasi akan ditangani saat user membuka aplikasi dari notifikasi.
                plantUseCase.checkAndNotifyPlant(plant) // Buat fungsi baru di UseCase
            }

            return Result.success()
        } catch (e: Exception) {
            // Jika terjadi error, kita bisa coba lagi nanti
            return Result.retry()
        }
    }
}