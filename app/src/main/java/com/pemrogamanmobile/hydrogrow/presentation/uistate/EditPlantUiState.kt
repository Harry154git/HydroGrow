package com.pemrogamanmobile.hydrogrow.presentation.uistate

import com.pemrogamanmobile.hydrogrow.domain.model.Plant

data class EditPlantUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,

    // Menyimpan objek plant asli untuk menjaga data yang tidak diedit (cth: plantingTime)
    val originalPlant: Plant? = null,

    // Properti untuk menampung nilai yang bisa diedit di UI
    val plantName: String = "",
    val harvestTime: String = "",
    val cupAmount: String = "", // ✅ DITAMBAHKAN
)
