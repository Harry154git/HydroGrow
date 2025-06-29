package com.pemrogamanmobile.hydrogrow.presentation.model

data class PlantUi(
    val id: String,
    val name: String,
    val harvestStatus: String,
    val imageUrl: String? = null
)
