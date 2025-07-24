package com.pemrogamanmobile.hydrogrow.data.repository

import android.net.Uri
import android.util.Log
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.PlantDao
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toEntity
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDto
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toEntity
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.AuthService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.ImageUploader
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.PlantService
import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import com.pemrogamanmobile.hydrogrow.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlinx.coroutines.flow.emitAll

class PlantRepositoryImpl @Inject constructor(
    private val dao: PlantDao,
    private val firestore: PlantService,
    private val authService: AuthService,
    private val imageUploader: ImageUploader
) : PlantRepository {

    // Helper to get the current user ID safely.
    private fun getUserId(): String {
        return authService.getCurrentUser()?.uid.orEmpty()
    }

    override suspend fun uploadPlantImage(uri: Uri): String {
        // Sekarang kita tentukan folder penyimpanannya adalah "gardens"
        return imageUploader.uploadImageToStorage(uri, "plants", getUserId())
    }

    override suspend fun insertPlant(plant: Plant) {
        // Convert domain model to local entity and save it.
        dao.insertPlant(plant.toEntity())
        // Convert domain model to DTO and upload to Firestore.
        firestore.uploadPlant(getUserId(), plant.gardenOwnerId, plant.toDto())
    }

    override suspend fun updatePlant(plant: Plant) {
        // Update the local entity.
        dao.updatePlant(plant.toEntity())
        // Update the document in Firestore.
        firestore.uploadPlant(getUserId(), plant.gardenOwnerId, plant.toDto())
    }

    override suspend fun deletePlant(plant: Plant, gardenId: String) {
        // Delete from local database first.
        dao.deletePlant(plant.toEntity())
        // Then, delete from Firestore.
        firestore.deletePlant(getUserId(), gardenId, plant.id)
    }

    /**
     * Implements a "Network-First, then Local" caching strategy.
     * 1. Attempts to fetch fresh data from Firestore.
     * 2. On success, deletes old local data and saves the new data (synchronizes).
     * 3. On network failure, it logs the error and proceeds.
     * 4. Finally, it emits a continuous stream of data from the local database (Room).
     * The UI always observes the local database as the single source of truth.
     */
    override fun getPlantsByGarden(gardenId: String): Flow<List<Plant>> = flow {
        try {
            // Ambil userId yang sedang aktif
            val userId = getUserId()

            // NETWORK: Fetch latest plants from Firestore.
            // Asumsi firestore.getPlants tidak berubah dan tetap butuh userId & gardenId
            val remotePlants = firestore.getPlants(userId, gardenId)

            // MAP: Convert remote DTOs to local entities.
            // PERBAIKAN: Berikan `userId` yang sudah didapat ke mapper `toEntity`.
            val entities = remotePlants.map { dto ->
                dto.toEntity(gardenId = gardenId, userId = userId)
            }

            // SAVE: Synchronize the local database with fresh data.
            dao.synchronizeGardenPlants(gardenId, entities)

        } catch (e: Exception) {
            // On network error, log the issue.
            Log.w("PlantRepository", "Failed to sync plants from network: ${e.message}")
        }

        // LOCAL (Single Source of Truth): Emit data dari database lokal.
        emitAll(dao.getPlantsByGardenId(gardenId).map { entities ->
            entities.map { it.toDomain() }
        })
    }


    override fun getPlantById(plantId: String): Flow<Plant?> {
        // This function continues to read directly from the cache,
        // which is assumed to be up-to-date by the getPlantsByGarden logic.
        return dao.getPlantById(plantId).map { it?.toDomain() }
    }

    /**
     * Mengimplementasikan strategi "Network-First" untuk SEMUA tanaman milik pengguna.
     * 1. Ambil data terbaru dari Firestore.
     * 2. Jika berhasil, sinkronkan database lokal (hapus semua data lama, masukkan data baru).
     * 3. Jika gagal, log error.
     * 4. Terakhir, selalu kembalikan data dari database lokal (Room) sebagai sumber kebenaran.
     */
    override suspend fun getAllPlants(): List<Plant> {
        val userId = getUserId()
        if (userId.isEmpty()) return emptyList()

        try {
            // NETWORK: Panggil service yang sudah diperbaiki.
            // Hasilnya sudah dalam bentuk List<Plant> (Domain Model).
            val remotePlants: List<Plant> = firestore.getAllUserPlants(userId)

            // MAP: Convert dari Domain Model ke Entity untuk disimpan ke Room.
            val entities = remotePlants.map { it.toEntity() }

            // SAVE: Sinkronkan database lokal dengan data baru.
            dao.synchronizeAllUserPlants(userId, entities)

        } catch (e: Exception) {
            Log.w("PlantRepository", "Failed to sync all plants from network: ${e.message}")
        }

        // LOCAL: Selalu kembalikan data dari database lokal.
        return dao.getAllUserPlants(userId).map { it.toDomain() }
    }
}