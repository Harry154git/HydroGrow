package com.pemrogamanmobile.hydrogrow.domain.repository

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    suspend fun uploadPlantImage(uri: Uri): String
    suspend fun insertPlant(plant: Plant)
    suspend fun updatePlant(plant: Plant)
    suspend fun deletePlant(plant: Plant, gardenId: String)
    fun getPlantsByGarden(gardenId: String): Flow<List<Plant>>
    fun getPlantById(plantId: String): Flow<Plant?>
    suspend fun getAllPlants(): List<Plant>
}

