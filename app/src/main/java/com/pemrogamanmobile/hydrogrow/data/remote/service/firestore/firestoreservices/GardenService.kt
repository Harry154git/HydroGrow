package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices

import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.remote.dto.GardenDto
import kotlinx.coroutines.tasks.await

class GardenService(private val db: FirebaseFirestore) {

    // Helper untuk mendapatkan referensi ke koleksi 'gardens' milik user
    private fun gardenCol(userId: String) =
        db.collection("users").document(userId).collection("gardens")

    /**
     * Mendapatkan semua garden milik user tertentu.
     */
    suspend fun getGardens(userId: String): List<GardenDto> {
        val snapshot = gardenCol(userId).get().await()
        return snapshot.documents.mapNotNull { it.toObject(GardenDto::class.java) }
    }

    /**
     * Mendapatkan satu garden spesifik berdasarkan ID-nya.
     */
    suspend fun getGardenById(userId: String, gardenId: String): GardenDto? {
        val doc = gardenCol(userId).document(gardenId).get().await()
        return doc.toObject(GardenDto::class.java)
    }

    /**
     * Menyimpan atau mengupdate satu garden milik user tertentu.
     */
    suspend fun uploadGarden(userId: String, garden: GardenDto) {
        gardenCol(userId).document(garden.id).set(garden).await()
    }

    /**
     * Menghapus satu garden milik user.
     */
    suspend fun deleteGarden(userId: String, gardenId: String) {
        gardenCol(userId).document(gardenId).delete().await()
    }
}