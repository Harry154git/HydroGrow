package com.pemrogamanmobile.hydrogrow.data.local.room.dao

import androidx.room.*
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PlantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlants(plants: List<PlantEntity>)

    @Update
    suspend fun updatePlant(plant: PlantEntity)

    @Delete
    suspend fun deletePlant(plant: PlantEntity)

    @Query("SELECT * FROM plant WHERE garden_owner_id = :gardenId")
    fun getPlantsByGardenId(gardenId: String): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plant WHERE id = :plantId LIMIT 1")
    fun getPlantById(plantId: String): Flow<PlantEntity?>

    @Query("DELETE FROM plant WHERE garden_owner_id = :gardenId")
    suspend fun deletePlantsByGardenId(gardenId: String)

    /**
     * Deletes all existing plants for a garden and inserts the new list
     * within a single database transaction. This ensures data consistency.
     */
    @Transaction
    suspend fun synchronizeGardenPlants(gardenId: String, plants: List<PlantEntity>) {
        deletePlantsByGardenId(gardenId)
        insertPlants(plants)
    }

    // --- TAMBAHAN: Fungsi baru untuk semua data per-User ---

    /**
     * Mengambil semua tanaman dari database lokal yang dimiliki oleh user tertentu.
     * Prasyarat: Tabel 'plant' harus memiliki kolom 'user_id'.
     */
    @Query("SELECT * FROM plant WHERE user_id = :userId")
    suspend fun getAllUserPlants(userId: String): List<PlantEntity>

    /**
     * Menghapus semua tanaman dari database lokal yang dimiliki oleh user tertentu.
     */
    @Query("DELETE FROM plant WHERE user_id = :userId")
    suspend fun deleteAllUserPlants(userId: String)

    /**
     * Menjalankan penghapusan dan penyisipan dalam satu transaksi atomik.
     * Ini untuk memastikan konsistensi data saat sinkronisasi dari Firestore.
     */
    @Transaction
    suspend fun synchronizeAllUserPlants(userId: String, plants: List<PlantEntity>) {
        deleteAllUserPlants(userId)
        insertPlants(plants)
    }
}
