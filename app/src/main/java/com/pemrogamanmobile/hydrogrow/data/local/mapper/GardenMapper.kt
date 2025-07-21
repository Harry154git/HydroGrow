package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.GardenEntity

fun GardenEntity.toDomain(): Garden = Garden(
    id = id,
    gardenName = gardenName,
    gardenSize = gardenSize,
    hydroponicType = hydroponicType,
    userOwnerId = userOwnerId,
    imageUrl = imageUrl
)

fun Garden.toEntity(): GardenEntity = GardenEntity(
    id = id,
    gardenName = gardenName,
    gardenSize = gardenSize,
    hydroponicType = hydroponicType,
    userOwnerId = userOwnerId,
    imageUrl = imageUrl
)

fun List<GardenEntity>.toDomainList(): List<Garden> = map { it.toDomain() }
fun List<Garden>.toEntityList(): List<GardenEntity> = map { it.toEntity() }