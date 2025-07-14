package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.remote.dto.PlantDto
import com.pemrogamanmobile.hydrogrow.domain.model.Plant

fun PlantDto.toDomain(): Plant = Plant(
    id = id,
    plantName = plantName,
    harvestTime = harvestTime,
    gardenOwnerId = gardenOwnerId,
    imageUrl = imageUrl
)

fun Plant.toDto(): PlantDto = PlantDto(
    id = id,
    plantName = plantName,
    harvestTime = harvestTime,
    gardenOwnerId = gardenOwnerId,
    imageUrl = imageUrl
)

fun List<PlantDto>.toDomainList(): List<Plant> = map { it.toDomain() }
fun List<Plant>.toDtoList(): List<PlantDto> = map { it.toDto() }