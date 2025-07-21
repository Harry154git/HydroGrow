package com.pemrogamanmobile.hydrogrow.domain.model

data class AiGardenPlan(
    val displayText: String,
    val hydroponicType: String,
    val recommendedPlants: List<String>, // Akan menjadi list kosong jika tidak minta rekomendasi
    val estimatedCost: Double,
    val landSizeM2: Double // [BARU] Untuk menyimpan luas lahan dalam meter persegi
)