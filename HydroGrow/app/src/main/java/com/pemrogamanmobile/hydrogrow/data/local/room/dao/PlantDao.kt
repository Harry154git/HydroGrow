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

    @Query("SELECT * FROM plant WHERE gardenOwnerId = :gardenId")
    fun getPlantsByGardenId(gardenId: String): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plant WHERE id = :plantId LIMIT 1")
    fun getPlantById(plantId: String): Flow<PlantEntity?>

    @Query("DELETE FROM plant")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<PlantEntity>)

    @Transaction
    suspend fun replaceAll(plants: List<PlantEntity>) {
        deleteAll()
        insertAll(plants)
    }
}
