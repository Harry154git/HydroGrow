package com.pemrogamanmobile.hydrogrow.data.local.room.dao

import androidx.room.*
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    // Menggunakan OnConflictStrategy.REPLACE agar bisa berfungsi sebagai insert dan update
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    // Fungsi baru untuk mengambil satu game milik user
    @Query("SELECT * FROM game WHERE userOwnerId = :userId LIMIT 1")
    fun getGameForUser(userId: String): Flow<GameEntity?>

    // Fungsi baru untuk menghapus semua data game milik user (untuk sinkronisasi)
    @Query("DELETE FROM game WHERE userOwnerId = :userId")
    suspend fun deleteGameForUser(userId: String)
}