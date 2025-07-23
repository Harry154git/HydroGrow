package com.pemrogamanmobile.hydrogrow.presentation.uistate

import com.pemrogamanmobile.hydrogrow.domain.model.Plant

sealed class PlantUiState {
    data object Loading : PlantUiState()
    data class Success(val plant: Plant) : PlantUiState()
    data class Error(val message: String) : PlantUiState()
}
