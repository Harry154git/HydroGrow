package com.pemrogamanmobile.hydrogrow.presentation.uistate

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.domain.model.Garden

data class AddPlantUiState(
    val plantName: String = "",
    val harvestTime: String = "",
    val cupAmount: String = "",
    val isLoading: Boolean = false,
    val selectedGardenId: String? = null,
    val availableGardens: List<Garden> = emptyList(),
    val imageUri: Uri? = null
)