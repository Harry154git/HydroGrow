package com.pemrogamanmobile.hydrogrow.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pemrogamanmobile.hydrogrow.data.local.room.converter.Converters
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.GardenDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.GardenEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.PlantDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PlantEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.ChatBotDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.ChatBotEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.GameDao
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.OnboardingPreferencesDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.GameEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.OnboardingPreferencesEntity

@Database(
    entities = [GardenEntity::class, PlantEntity::class, ChatBotEntity::class, GameEntity::class, OnboardingPreferencesEntity::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gardenDao(): GardenDao
    abstract fun plantDao(): PlantDao
    abstract fun chatBotDao(): ChatBotDao
    abstract fun gameDao(): GameDao
    abstract fun onboardingPreferencesDao(): OnboardingPreferencesDao
}