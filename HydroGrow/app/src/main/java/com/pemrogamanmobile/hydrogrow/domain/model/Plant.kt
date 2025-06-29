package com.pemrogamanmobile.hydrogrow.domain.model

data class Plant(
    val id: String,
    val plantName: String,
    val nutrientsUsed: String,
    val harvestTime: String,
    val gardenOwnerId: String
)