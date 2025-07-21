package com.pemrogamanmobile.hydrogrow.presentation.ui.uistate

import com.pemrogamanmobile.hydrogrow.presentation.model.GardenUi
import com.pemrogamanmobile.hydrogrow.presentation.model.PlantUi

data class GardenUiState(
    val garden: GardenUi? = null,
    val plants: List<PlantUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
