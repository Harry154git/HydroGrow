package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.PlantUseCase
import com.pemrogamanmobile.hydrogrow.presentation.ui.uistate.PlantUiState
import com.pemrogamanmobile.hydrogrow.presentation.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val plantUseCase: PlantUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlantUiState>(PlantUiState.Loading)
    val uiState: StateFlow<PlantUiState> = _uiState.asStateFlow()

    fun loadPlantById(plantId: String) {
        viewModelScope.launch {
            _uiState.value = PlantUiState.Loading

            plantUseCase.getPlantById(plantId).collect { plant ->
                if (plant != null) {
                    _uiState.value = PlantUiState.Success(plant.toUi())
                } else {
                    _uiState.value = PlantUiState.Error("Tanaman tidak ditemukan")
                }
            }
        }
    }
}