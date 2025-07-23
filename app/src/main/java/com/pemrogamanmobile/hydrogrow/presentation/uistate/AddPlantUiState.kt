package com.pemrogamanmobile.hydrogrow.presentation.uistate

import com.pemrogamanmobile.hydrogrow.domain.model.Garden

data class AddPlantUiState(
    val plantName: String = "",
    val harvestTime: String = "",
    val cupAmount: String = "", // ✅ DITAMBAHKAN - untuk menampung input jumlah cup
    val selectedGardenId: String? = null, // Diubah ke nullable untuk state awal yang lebih aman
    val availableGardens: List<Garden> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)