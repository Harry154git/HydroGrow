package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices

import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.remote.dto.PlantDto
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
}