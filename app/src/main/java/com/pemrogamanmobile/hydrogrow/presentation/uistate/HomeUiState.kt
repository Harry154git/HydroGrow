package com.pemrogamanmobile.hydrogrow.presentation.uistate

import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.domain.model.User

data class HomeUiState(
    val user: User? = null,
    val gardens: List<Garden> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)