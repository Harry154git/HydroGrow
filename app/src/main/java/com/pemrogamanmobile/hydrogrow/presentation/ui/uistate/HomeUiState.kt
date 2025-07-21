package com.pemrogamanmobile.hydrogrow.presentation.ui.uistate

import com.pemrogamanmobile.hydrogrow.presentation.model.GardenUi
import com.pemrogamanmobile.hydrogrow.presentation.model.PlantUi
import com.pemrogamanmobile.hydrogrow.presentation.model.UserUi


data class HomeUiState(
    val user: UserUi? = null,
    val gardens: List<GardenUi> = emptyList(),
    val plants: List<PlantUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
