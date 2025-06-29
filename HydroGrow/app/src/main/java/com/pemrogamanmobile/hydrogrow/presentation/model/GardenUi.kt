package com.pemrogamanmobile.hydrogrow.presentation.model

data class GardenUi(
    val id: String,
    val name: String,
    val size: Double,
    val hydroponicType: String,
    val userOwnerId: String,
    val imageUrl: String? = null
)