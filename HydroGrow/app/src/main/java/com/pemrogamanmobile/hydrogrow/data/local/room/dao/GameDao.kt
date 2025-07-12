package com.pemrogamanmobile.hydrogrow.data.local.room.dao

import androidx.room.*
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Update
    suspend fun updateGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("SELECT * FROM game WHERE id = :gameId LIMIT 1")
    suspend fun getGameById(gameId: String): GameEntity?

    @Query("SELECT * FROM game WHERE userOwnerId = :userId")
    fun getGameByUserId(userId: String): Flow<List<GameEntity>>

    @Query("DELETE FROM game")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(game: List<GameEntity>)

    @Transaction
    suspend fun replaceAll(game: List<GameEntity>) {
        deleteAll()
        insertAll(game)
    }
}