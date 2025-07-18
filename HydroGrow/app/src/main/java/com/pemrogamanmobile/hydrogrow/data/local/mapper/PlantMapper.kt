package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PlantEntity

fun PlantEntity.toDomain(): Plant = Plant(
    id = id,
    plantName = plantName,
    harvestTime = harvestTime,
    gardenOwnerId = gardenOwnerId,
    imageUrl = imageUrl
)

fun Plant.toEntity(): PlantEntity = PlantEntity(
    id = id,
    plantName = plantName,
    harvestTime = harvestTime,
    gardenOwnerId = gardenOwnerId,
    imageUrl = imageUrl
)

fun List<PlantEntity>.toDomainList(): List<Plant> = map { it.toDomain() }
fun List<Plant>.toEntityList(): List<PlantEntity> = map { it.toEntity() }