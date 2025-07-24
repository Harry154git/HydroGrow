package com.pemrogamanmobile.hydrogrow.presentation.uistate

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.domain.model.Plant

data class EditPlantUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,

    val originalPlant: Plant? = null,

    val plantName: String = "",
    val harvestTime: String = "",
    val cupAmount: String = "",

    val newImageUri: Uri? = null
)