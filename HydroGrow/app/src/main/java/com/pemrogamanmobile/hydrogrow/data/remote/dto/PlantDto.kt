package com.pemrogamanmobile.hydrogrow.data.remote.dto

data class PlantDto(
    val id: String = "",
    val plantName: String = "",
    val harvestTime: String = "",
    val gardenOwnerId: String = "",
    val imageUrl: String? = ""
)