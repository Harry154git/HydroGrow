package com.pemrogamanmobile.hydrogrow.domain.model

data class AiGardenPlan(
    val displayText: String,
    val hydroponicType: String,
    val recommendedPlants: List<String>,
    val estimatedCost: Double,
    val landSizeM2: Double
)