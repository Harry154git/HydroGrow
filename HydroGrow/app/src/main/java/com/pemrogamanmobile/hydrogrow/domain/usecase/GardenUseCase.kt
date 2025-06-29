package com.pemrogamanmobile.hydrogrow.domain.usecase

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.domain.repository.GardenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GardenUseCase @Inject constructor(
    private val repository: GardenRepository
) {
    suspend fun insertGarden(garden: Garden) {
        repository.insertGarden(garden)
    }

    suspend fun updateGarden(garden: Garden) {
        repository.updateGarden(garden)
    }

    suspend fun uploadGardenImage(uri: Uri): String {
        return repository.uploadGardenImage(uri)
    }

    suspend fun deleteGarden(garden: Garden) {
        repository.deleteGarden(garden)
    }

    fun getAllGardens(): Flow<List<Garden>> {
        return repository.getAllGardens()
    }

    fun getGardensByUserId(userId: String): Flow<List<Garden>> {
        return repository.getGardensByUserId(userId)
    }

    suspend fun getGardenById(gardenId: String): Garden? {
        return repository.getGardenById(gardenId)
    }
}