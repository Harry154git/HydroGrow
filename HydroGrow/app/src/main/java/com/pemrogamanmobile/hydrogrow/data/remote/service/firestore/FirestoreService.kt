package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.remote.dto.GardenDto
import com.pemrogamanmobile.hydrogrow.data.remote.dto.PlantDto
import kotlinx.coroutines.tasks.await

class FirestoreService(private val db: FirebaseFirestore) {

    private val userCol = db.collection("user")

    // --------------------
    // User/Profile Section
    // --------------------

    suspend fun getProfile(uid: String): Map<String, Any>? =
        userCol.document(uid).get().await().data

    suspend fun updateProfile(uid: String, data: Map<String, Any>) {
        userCol.document(uid).set(data).await()
    }

    // --------------------
    // Garden Section (Nested)
    // --------------------

    // Mendapatkan semua gardens milik user tertentu
    suspend fun getGardens(userId: String): List<GardenDto> {
        val gardenSnapshot = userCol.document(userId)
            .collection("gardens")
            .get()
            .await()

        return gardenSnapshot.documents.mapNotNull { it.toObject(GardenDto::class.java) }
    }

    // Menyimpan atau mengupdate satu garden milik user tertentu
    suspend fun uploadGarden(userId: String, garden: GardenDto) {
        userCol.document(userId)
            .collection("gardens")
            .document(garden.id)
            .set(garden)
            .await()
    }

    // Menghapus satu garden milik user
    suspend fun deleteGarden(userId: String, gardenId: String) {
        userCol.document(userId)
            .collection("gardens")
            .document(gardenId)
            .delete()
            .await()
    }

    suspend fun getGardenById(userId: String, gardenId: String): GardenDto? {
        val doc = userCol.document(userId)
            .collection("gardens")
            .document(gardenId)
            .get()
            .await()

        return doc.toObject(GardenDto::class.java)
    }


    // --------------------
    // Plant Section (Nested in Gardens)
    // --------------------

    suspend fun getPlants(userId: String, gardenId: String): List<PlantDto> {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("user")
            .document(userId)
            .collection("gardens")
            .document(gardenId)
            .collection("plants")
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(PlantDto::class.java) }
    }

    suspend fun uploadPlant(userId: String, gardenId: String, plant: PlantDto) {
        FirebaseFirestore.getInstance()
            .collection("user")
            .document(userId)
            .collection("gardens")
            .document(gardenId)
            .collection("plants")
            .document(plant.id)
            .set(plant)
            .await()
    }

    suspend fun deletePlant(userId: String, gardenId: String, plantId: String) {
        FirebaseFirestore.getInstance()
            .collection("user")
            .document(userId)
            .collection("gardens")
            .document(gardenId)
            .collection("plants")
            .document(plantId)
            .delete()
            .await()
    }
}