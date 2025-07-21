package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant")
data class PlantEntity(
    @PrimaryKey
    val id: String,
    val plantName: String,
    val harvestTime: String,
    @ColumnInfo(name = "garden_owner_id") // ID dari kebun tempat ia ditanam
    val gardenOwnerId: String,
    val imageUrl: String?,
    val plantingTime: Long,
    val cupAmount: Int,
    @ColumnInfo(name = "user_id", index = true) // TAMBAHAN PENTING: ID dari user pemilik
    val userId: String
)