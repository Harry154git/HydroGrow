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
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.PostingDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.GameDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.CommentEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.GameEntity

@Database(
    entities = [GardenEntity::class, PlantEntity::class, ChatBotEntity::class, PostingEntity::class, GameEntity::class, CommentEntity::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gardenDao(): GardenDao
    abstract fun plantDao(): PlantDao
    abstract fun chatBotDao(): ChatBotDao
    abstract fun postingDao(): PostingDao
    abstract fun gameDao(): GameDao
}