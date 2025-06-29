package com.pemrogamanmobile.hydrogrow.presentation.ui.uistate

import com.pemrogamanmobile.hydrogrow.presentation.model.UserUi

data class UserUiState(
    val user: UserUi? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)