package com.pemrogamanmobile.hydrogrow.domain.model

data class Garden(
    val id: String,
    val gardenName: String,
    val gardenSize: Double,
    val hydroponicType: String,
    val userOwnerId: String,
    val imageUrl: String? = null
)