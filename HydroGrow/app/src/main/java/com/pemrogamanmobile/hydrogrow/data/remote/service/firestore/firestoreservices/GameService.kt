package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices

import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.remote.dto.GameDto
import kotlinx.coroutines.tasks.await

class GameService(private val db: FirebaseFirestore) {

    private fun gameDoc(userId: String) = db.collection("users").document(userId)

    // Mengambil data game milik user
    suspend fun getGame(userId: String): GameDto? {
        // Asumsi hanya ada 1 dokumen game per user
        val snapshot = gameDoc(userId).collection("game").limit(1).get().await()
        return snapshot.documents.firstOrNull()?.toObject(GameDto::class.java)
    }

    // Mengupdate data game milik user
    suspend fun updateGame(userId: String, game: GameDto) {
        gameDoc(userId).collection("game").document(game.id).set(game).await()
    }
}