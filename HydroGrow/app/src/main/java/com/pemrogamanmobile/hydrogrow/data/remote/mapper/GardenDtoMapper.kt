package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.remote.dto.GardenDto
import com.pemrogamanmobile.hydrogrow.domain.model.Garden

fun GardenDto.toDomain(): Garden = Garden(
    id = id,
    gardenName = gardenName,
    gardenSize = gardenSize,
    hydroponicType = hydroponicType,
    userOwnerId = userOwnerId,
    imageUrl = imageUrl
)


fun Garden.toDto(): GardenDto = GardenDto(
    id = id,
    gardenName = gardenName,
    gardenSize = gardenSize,
    hydroponicType = hydroponicType,
    userOwnerId = userOwnerId,
    imageUrl = imageUrl
)

fun List<GardenDto>.toDomainList(): List<Garden> = map { it.toDomain() }
fun List<Garden>.toDtoList(): List<GardenDto> = map { it.toDto() }