package com.pemrogamanmobile.hydrogrow.presentation.uistate

import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.domain.model.User

data class HomeUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val gardens: List<Garden> = emptyList(),
    val cupAmount: Int = 0,
    val error: String? = null
)