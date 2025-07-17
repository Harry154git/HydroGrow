package com.pemrogamanmobile.hydrogrow.domain.repository

import com.pemrogamanmobile.hydrogrow.domain.model.Game
import kotlinx.coroutines.flow.Flow

/**
 * Interface untuk mengelola data game.
 * Didesain untuk hubungan satu user memiliki satu data game.
 */
interface GameRepository {

    /**
     * Membuat data game baru atau memperbarui yang sudah ada.
     * Menggabungkan logika insert dan update.
     */
    suspend fun createOrUpdateGame(game: Game)

    /**
     * Menghapus data game yang ada.
     */
    suspend fun deleteGame(game: Game)

    /**
     * Mendapatkan data game milik user sebagai Flow.
     * Mengembalikan satu objek Game yang bisa null jika tidak ada.
     * Ini menjadi pengganti getAllGame() dan getGameById().
     */
    fun getGame(): Flow<Game?>
}