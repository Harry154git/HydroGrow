package com.pemrogamanmobile.hydrogrow.presentation.uistate

import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.domain.model.Plant

// Pastikan tipe data menggunakan model dari domain
data class GardenUiState(
    val isLoading: Boolean = false,
    val garden: Garden? = null,        // Diubah dari GardenUi
    val plants: List<Plant> = emptyList(), // Diubah dari List<PlantUi>
    val error: String? = null
)