package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PlantEntity
import com.pemrogamanmobile.hydrogrow.data.remote.dto.PlantDto
import com.pemrogamanmobile.hydrogrow.domain.model.Plant

fun PlantDto.toDomain(gardenId: String, userId: String): Plant {
    return Plant(
        id = this.id,
        plantName = this.plantName,
        harvestTime = this.harvestTime,
        gardenOwnerId = gardenId, // Diambil dari parent
        imageUrl = this.imageUrl,
        plantingTime = this.plantingTime,
        cupAmount = this.cupAmount,
        userOwnerId = userId // Diambil dari parent garden
    )
}

fun Plant.toDto(): PlantDto {
    return PlantDto(
        id = this.id,
        plantName = this.plantName,
        harvestTime = this.harvestTime,
        imageUrl = this.imageUrl,
        plantingTime = this.plantingTime,
        cupAmount = this.cupAmount
    )
}

fun PlantDto.toEntity(gardenId: String, userId: String): PlantEntity {
    return PlantEntity(
        id = this.id,
        plantName = this.plantName,
        harvestTime = this.harvestTime,
        gardenOwnerId = gardenId, // Diambil dari parameter
        imageUrl = this.imageUrl,
        plantingTime = this.plantingTime, // Diambil dari DTO
        cupAmount = this.cupAmount,       // Diambil dari DTO
        userId = userId                  // Diambil dari parameter
    )
}