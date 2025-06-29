package com.pemrogamanmobile.hydrogrow.presentation.ui.uistate

import com.pemrogamanmobile.hydrogrow.domain.model.Garden

data class EditPlantUiState(
    val plantId: String = "",
    val gardenOwnerId: String = "",
    val availableGardens: List<Garden> = emptyList(),
    val availableNutrients: List<String> = emptyList(),

    val plantName: String = "",
    val nutrientsUsed: String = "",
    val harvestTime: String = "",

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)


