package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game")
data class GameEntity (
    @PrimaryKey val id: String,
    val userOwnerId: String,
    val cup: Int,
)