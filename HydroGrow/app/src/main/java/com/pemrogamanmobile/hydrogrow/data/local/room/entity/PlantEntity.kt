package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant")
data class PlantEntity(
    @PrimaryKey val id: String,
    val plantName: String,
    val nutrientsUsed: String,
    val harvestTime: String,
    val gardenOwnerId: String,
)