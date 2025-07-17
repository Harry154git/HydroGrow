package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices

import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.remote.dto.GameDto
import kotlinx.coroutines.tasks.await

class GameService(private val db: FirebaseFirestore) {

    // Helper untuk mendapatkan referensi ke koleksi 'game' milik user
    private fun gameCollectionRef(userId: String) =
        db.collection("users").document(userId).collection("game")

    /**
     * Membuat atau mengupdate data game milik user.
     * Menggunakan game.id sebagai ID dokumen.
     */
    suspend fun createOrUpdateGame(userId: String, game: GameDto) {
        // Pastikan game.id tidak kosong, karena akan digunakan sebagai ID dokumen.
        if (game.id.isBlank()) {
            throw IllegalArgumentException("Game ID cannot be blank.")
        }
        // Menyimpan/mengupdate dokumen dengan ID yang berasal dari objek game itu sendiri.
        gameCollectionRef(userId).document(game.id).set(game).await()
    }

    /**
     * Mengambil data game milik user.
     * Karena hanya ada 1 dokumen, kita ambil dokumen pertama dari koleksi.
     */
    suspend fun getGame(userId: String): GameDto? {
        val snapshot = gameCollectionRef(userId).limit(1).get().await()
        // Mengambil dokumen pertama jika ada, lalu konversi ke GameDto
        return snapshot.documents.firstOrNull()?.toObject(GameDto::class.java)
    }

    /**
     * Menghapus data game milik user.
     * Memerlukan gameId untuk mengetahui dokumen mana yang harus dihapus.
     */
    suspend fun deleteGame(userId: String, gameId: String) {
        if (gameId.isBlank()) {
            throw IllegalArgumentException("Game ID to delete cannot be blank.")
        }
        gameCollectionRef(userId).document(gameId).delete().await()
    }
}