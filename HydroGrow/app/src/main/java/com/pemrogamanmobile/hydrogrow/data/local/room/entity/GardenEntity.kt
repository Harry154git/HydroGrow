package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garden")
data class GardenEntity(
    @PrimaryKey val id: String,
    val gardenName: String,
    val gardenSize: Double,
    val hydroponicType: String,
    val userOwnerId: String,
    val imageUrl: String? = null
)