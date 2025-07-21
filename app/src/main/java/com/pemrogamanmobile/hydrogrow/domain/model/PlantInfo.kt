package com.pemrogamanmobile.hydrogrow.domain.model

data class PlantInfo(
    val scientificName: String,
    val commonName: String,
    val score: Double // Skor keyakinan dari 0.0 hingga 1.0
)