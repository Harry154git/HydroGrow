package com.pemrogamanmobile.hydrogrow.data.remote.dto

data class GardenDto(
    val id: String = "",
    val gardenName: String = "",
    val gardenSize: Double = 0.0,
    val hydroponicType: String = "",
    val userOwnerId: String = "",
    val imageUrl: String? = ""
)