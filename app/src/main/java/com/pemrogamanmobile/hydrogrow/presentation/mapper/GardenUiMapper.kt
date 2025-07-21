package com.pemrogamanmobile.hydrogrow.presentation.mapper

import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.presentation.model.GardenUi

fun Garden.toUi() = GardenUi(
    id = id,
    name = gardenName,
    size = gardenSize,
    hydroponicType = hydroponicType,
    userOwnerId = userOwnerId,
    imageUrl = imageUrl
)

fun GardenUi.toDomain() = Garden(
    id = id,
    gardenName = name,
    gardenSize = size,
    hydroponicType = hydroponicType,
    userOwnerId = userOwnerId,
    imageUrl = imageUrl
)


