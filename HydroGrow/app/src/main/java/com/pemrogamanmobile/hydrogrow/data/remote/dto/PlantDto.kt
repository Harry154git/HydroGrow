package com.pemrogamanmobile.hydrogrow.data.remote.dto

// DTO ini hanya merepresentasikan data yang ADA di dokumen 'plant' di Firestore.
// Jadi tidak perlu userOwnerId di sini.
data class PlantDto(
    val id: String = "",
    val plantName: String = "",
    val harvestTime: String = "",
    // gardenOwnerId juga tidak ada di sini, karena sudah implisit dari parent collection
    val imageUrl: String? = null,
    val plantingTime: Long = 0L,
    val cupAmount: Int = 0
)