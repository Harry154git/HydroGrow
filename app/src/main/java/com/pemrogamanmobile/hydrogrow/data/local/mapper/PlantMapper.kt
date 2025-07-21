package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PlantEntity

fun PlantEntity.toDomain(): Plant {
    return Plant(
        id = this.id,
        plantName = this.plantName,
        harvestTime = this.harvestTime,
        gardenOwnerId = this.gardenOwnerId,
        imageUrl = this.imageUrl,
        plantingTime = this.plantingTime,
        cupAmount = this.cupAmount,
        userOwnerId = this.userId
    )
}

// Mengubah Model dari domain/UI menjadi Entity untuk disimpan ke database
fun Plant.toEntity(): PlantEntity {
    return PlantEntity(
        id = this.id,
        plantName = this.plantName,
        harvestTime = this.harvestTime,
        gardenOwnerId = this.gardenOwnerId,
        imageUrl = this.imageUrl,
        plantingTime = this.plantingTime,
        cupAmount = this.cupAmount,
        userId = this.userOwnerId
    )
}

// Jika kamu menggunakan list
fun List<PlantEntity>.toDomain(): List<Plant> = this.map { it.toDomain() }
fun List<Plant>.toEntity(): List<PlantEntity> = this.map { it.toEntity() }