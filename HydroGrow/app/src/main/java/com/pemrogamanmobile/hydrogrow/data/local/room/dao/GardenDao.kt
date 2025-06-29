package com.pemrogamanmobile.hydrogrow.data.local.room.dao

import androidx.room.*
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.GardenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGarden(garden: GardenEntity)

    @Update
    suspend fun updateGarden(garden: GardenEntity)

    @Delete
    suspend fun deleteGarden(garden: GardenEntity)

    @Query("SELECT * FROM garden")
    fun getAllGardens(): Flow<List<GardenEntity>>

    @Query("SELECT * FROM garden WHERE id = :gardenId LIMIT 1")
    suspend fun getGardenById(gardenId: String): GardenEntity?

    @Query("SELECT * FROM garden WHERE userOwnerId = :userId")
    fun getGardensByUserId(userId: String): Flow<List<GardenEntity>>

    @Query("DELETE FROM garden")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gardens: List<GardenEntity>)

    @Transaction
    suspend fun replaceAll(gardens: List<GardenEntity>) {
        deleteAll()
        insertAll(gardens)
    }

}
