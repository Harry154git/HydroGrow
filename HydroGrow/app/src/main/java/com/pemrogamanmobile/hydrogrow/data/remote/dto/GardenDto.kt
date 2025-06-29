package com.pemrogamanmobile.hydrogrow.data.remote.dto

data class GardenDto(
    val id: String = "",
    val gardenName: String = "",
    val size: Double = 0.0,
    val type: String = "",
    val userId: String = "",
    val imageUrl: String? = null
)