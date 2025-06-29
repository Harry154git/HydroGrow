package com.pemrogamanmobile.hydrogrow.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.UserDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.UserEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.GardenDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.GardenEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.PlantDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PlantEntity

@Database(
    entities = [GardenEntity::class, UserEntity::class, PlantEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun gardenDao(): GardenDao
    abstract fun plantDao(): PlantDao
}