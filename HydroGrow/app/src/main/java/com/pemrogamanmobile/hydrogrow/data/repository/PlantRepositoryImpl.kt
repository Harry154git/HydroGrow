package com.pemrogamanmobile.hydrogrow.data.repository

import com.pemrogamanmobile.hydrogrow.data.local.room.dao.PlantDao
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toEntity
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDto
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toEntity
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.AuthService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.PlantService
import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import com.pemrogamanmobile.hydrogrow.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.emitAll

class PlantRepositoryImpl @Inject constructor(
    private val dao: PlantDao,
    private val firestore: PlantService,
    private val authService: AuthService
) : PlantRepository {

    private fun getUserId(): String {
        return authService.getCurrentUser()?.uid.orEmpty()
    }

    override suspend fun insertPlant(plant: Plant) {
        val entity = plant.toEntity()
        dao.insertPlant(entity)
        firestore.uploadPlant(getUserId(), plant.gardenOwnerId, plant.toDto())
    }

    override suspend fun updatePlant(plant: Plant) {
        val entity = plant.toEntity()
        dao.updatePlant(entity)
        firestore.uploadPlant(getUserId(), plant.gardenOwnerId, plant.toDto())
    }

    override suspend fun deletePlant(plant: Plant, gardenId: String) {
        dao.deletePlant(plant.toEntity())
        firestore.deletePlant(getUserId(), gardenId, plant.id)
    }

    override fun getPlantsByGarden(gardenId: String): Flow<List<Plant>> = flow {
        // Emit local first
        val localPlants = dao.getPlantsByGardenId(gardenId).firstOrNull()
        if (!localPlants.isNullOrEmpty()) {
            emit(localPlants.map { it.toDomain() })
        } else {
            // Fetch from Firestore if local empty
            val remotePlants = firestore.getPlants(getUserId(), gardenId)
            val entities = remotePlants.map { it.toEntity() }
            dao.insertPlants(entities)
            emit(entities.map { it.toDomain() })
        }

        // Listen to DAO changes
        emitAll(dao.getPlantsByGardenId(gardenId).map { list -> list.map { it.toDomain() } })
    }

    override fun getPlantById(plantId: String): Flow<Plant?> {
        return dao.getPlantById(plantId).map { it?.toDomain() }
    }
}
