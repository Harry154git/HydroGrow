package com.pemrogamanmobile.hydrogrow.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pemrogamanmobile.hydrogrow.domain.usecase.plant.PlantUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PlantCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val plantUseCase: PlantUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val allPlants = plantUseCase.getAllPlants()
            allPlants.forEach { plant ->
                plantUseCase.checkAndNotifyPlant(plant)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}