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

    // FUNGSI BARU: Untuk memasukkan list kebun sekaligus (dibutuhkan untuk sinkronisasi)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGardens(gardens: List<GardenEntity>)

    // FUNGSI BARU: Menghapus semua kebun milik user tertentu (dibutuhkan untuk sinkronisasi)
    @Query("DELETE FROM garden WHERE userOwnerId = :userId")
    suspend fun deleteAllGardensByUserId(userId: String)

    // FUNGSI BARU: Menghapus kebun berdasarkan ID-nya (dibutuhkan untuk getGardenById)
    @Query("DELETE FROM garden WHERE id = :gardenId")
    suspend fun deleteGardenById(gardenId: String)

}
