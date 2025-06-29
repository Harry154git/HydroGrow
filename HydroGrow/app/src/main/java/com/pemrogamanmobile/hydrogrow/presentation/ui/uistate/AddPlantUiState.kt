package com.pemrogamanmobile.hydrogrow.presentation.ui.uistate

import com.pemrogamanmobile.hydrogrow.domain.model.Garden

data class AddPlantUiState(
    val plantName: String = "",
    val nutrientsUsed: String = "",
    val harvestTime: String = "",
    val selectedGardenId: String = "",
    val availableGardens: List<Garden> = emptyList(),
    val nutrientLocked: Boolean = false,
    val nutrientOptions: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
