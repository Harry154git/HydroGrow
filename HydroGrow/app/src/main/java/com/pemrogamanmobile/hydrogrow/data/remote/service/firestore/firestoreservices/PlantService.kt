package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices

import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.remote.dto.GardenDto
import com.pemrogamanmobile.hydrogrow.data.remote.dto.PlantDto
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class PlantService(private val db: FirebaseFirestore) {

    // Helper untuk mendapatkan referensi ke koleksi 'plants' di dalam garden tertentu
    private fun plantCol(userId: String, gardenId: String) =
        db.collection("users").document(userId)
            .collection("gardens").document(gardenId)
            .collection("plants")

    /**
     * Mendapatkan semua tanaman dari sebuah garden spesifik.
     */
    suspend fun getPlants(userId: String, gardenId: String): List<PlantDto> {
        val snapshot = plantCol(userId, gardenId).get().await()
        return snapshot.documents.mapNotNull { it.toObject(PlantDto::class.java) }
    }

    /**
     * Menyimpan atau mengupdate data tanaman di dalam sebuah garden.
     */
    suspend fun uploadPlant(userId: String, gardenId: String, plant: PlantDto) {
        plantCol(userId, gardenId).document(plant.id).set(plant).await()
    }

    /**
     * Menghapus data tanaman dari sebuah garden.
     */
    suspend fun deletePlant(userId: String, gardenId: String, plantId: String) {
        plantCol(userId, gardenId).document(plantId).delete().await()
    }

    suspend fun getAllUserPlants(userId: String): List<Plant> = coroutineScope {
        // 1. Ambil semua garden milik user
        val gardensSnapshot = db.collection("users").document(userId)
            .collection("gardens").get().await()
        val gardens = gardensSnapshot.toObjects(GardenDto::class.java)

        // 2. Untuk setiap garden, ambil semua plants-nya secara bersamaan (concurrent)
        val allPlantsDeferred = gardens.map { garden ->
            async {
                val plantsSnapshot = db.collection("users").document(userId)
                    .collection("gardens").document(garden.id)
                    .collection("plants").get().await()

                val plantDtos = plantsSnapshot.toObjects(PlantDto::class.java)

                // 3. Langsung mapping ke Domain Model karena kita punya semua data yang diperlukan
                plantDtos.map { dto -> dto.toDomain(gardenId = garden.id, userId = userId) }
            }
        }

        // 4. Tunggu semua proses selesai dan gabungkan hasilnya menjadi satu list
        allPlantsDeferred.awaitAll().flatten()
    }
}