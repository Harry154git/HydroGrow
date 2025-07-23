package com.pemrogamanmobile.hydrogrow.presentation.uistate

import com.pemrogamanmobile.hydrogrow.domain.model.User

data class UserUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
    // val isSuccess: Boolean = false ✅ DIHAPUS
)