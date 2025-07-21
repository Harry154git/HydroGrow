package com.pemrogamanmobile.hydrogrow.presentation.mapper

import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import com.pemrogamanmobile.hydrogrow.presentation.model.PlantUi

fun Plant.toUi(): PlantUi {
    return PlantUi(
        id = id,
        name = plantName,
        harvestStatus = harvestTime,
        imageUrl = null
    )
}