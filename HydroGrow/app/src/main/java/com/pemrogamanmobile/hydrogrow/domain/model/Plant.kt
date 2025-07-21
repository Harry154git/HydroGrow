package com.pemrogamanmobile.hydrogrow.domain.model

data class Plant(
    val id: String,
    val plantName: String,
    val harvestTime: String,
    val gardenOwnerId: String,
    val userOwnerId: String, // <-- TAMBAHAN
    val imageUrl: String?,
    val plantingTime: Long, // Waktu tanam dalam Linux timestamp (milidetik)
    val cupAmount: Int      // Jumlah cup per tanaman
)