package com.pemrogamanmobile.hydrogrow.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.UserDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.UserEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.GardenDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.GardenEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.PlantDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PlantEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.ChatBotDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.ChatBotEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.PostingDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.GameDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.GameEntity

@Database(
    entities = [GardenEntity::class, UserEntity::class, PlantEntity::class, ChatBotEntity::class, PostingEntity::class, GameEntity::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(ChatBotTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun gardenDao(): GardenDao
    abstract fun plantDao(): PlantDao
    abstract fun ChatBotDao(): ChatBotDao
    abstract fun PostingDao(): PostingDao
    abstract fun GameDao(): GameDao
}