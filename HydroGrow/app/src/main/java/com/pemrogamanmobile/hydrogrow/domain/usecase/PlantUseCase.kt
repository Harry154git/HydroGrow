package com.pemrogamanmobile.hydrogrow.domain.usecase

import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import com.pemrogamanmobile.hydrogrow.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlantUseCase @Inject constructor(
    private val repository: PlantRepository
) {
    suspend fun insertPlant(plant: Plant) {
        repository.insertPlant(plant)
    }

    suspend fun updatePlant(plant: Plant) {
        repository.updatePlant(plant)
    }

    suspend fun deletePlant(plant: Plant, gardenId: String) {
        repository.deletePlant(plant, gardenId)
    }

    fun getPlantsByGarden(gardenId: String): Flow<List<Plant>> {
        return repository.getPlantsByGarden(gardenId)
    }

    fun getPlantById(plantId: String): Flow<Plant?> = repository.getPlantById(plantId)
}

