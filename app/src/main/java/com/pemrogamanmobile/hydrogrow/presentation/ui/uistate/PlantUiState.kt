package com.pemrogamanmobile.hydrogrow.presentation.ui.uistate

import com.pemrogamanmobile.hydrogrow.presentation.model.PlantUi

sealed class PlantUiState {
    object Loading : PlantUiState()
    data class Success(val plant: PlantUi) : PlantUiState()
    data class Error(val message: String) : PlantUiState()
}
