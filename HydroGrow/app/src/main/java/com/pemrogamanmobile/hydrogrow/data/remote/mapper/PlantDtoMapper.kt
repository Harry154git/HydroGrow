package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PlantEntity
import com.pemrogamanmobile.hydrogrow.data.remote.dto.PlantDto
import com.pemrogamanmobile.hydrogrow.domain.model.Plant

fun PlantDto.toDomain(): Plant = Plant(
    id = id,
    plantName = plantName,
    nutrientsUsed = nutrientsUsed,
    harvestTime = harvestTime,
    gardenOwnerId = gardenId
)

fun Plant.toDto(): PlantDto = PlantDto(
    id = id,
    plantName = plantName,
    nutrientsUsed = nutrientsUsed,
    harvestTime = harvestTime,
    gardenId = gardenOwnerId
)

fun PlantDto.toEntity(): PlantEntity {
    return PlantEntity(
        id = id,
        plantName = plantName,
        nutrientsUsed = nutrientsUsed,
        harvestTime = harvestTime,
        gardenOwnerId = gardenId
    )
}

fun List<PlantDto>.toDomainList(): List<Plant> = map { it.toDomain() }
fun List<Plant>.toDtoList(): List<PlantDto> = map { it.toDto() }