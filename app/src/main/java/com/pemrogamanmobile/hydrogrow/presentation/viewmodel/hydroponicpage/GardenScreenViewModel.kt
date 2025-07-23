package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.garden.GardenUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.plant.PlantUseCase
import com.pemrogamanmobile.hydrogrow.presentation.uistate.GardenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class GardenViewModel @Inject constructor(
    private val gardenUseCase: GardenUseCase,
    private val plantUseCase: PlantUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GardenUiState())
    val uiState: StateFlow<GardenUiState> = _uiState.asStateFlow()

    fun loadGardenById(gardenId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Ambil data kebun dari use case
                val garden = gardenUseCase.getGardenById(gardenId)

                // Update state dengan data kebun yang didapat
                _uiState.update { it.copy(garden = garden) }

                // Jika kebun ditemukan, muat tanaman yang ada di dalamnya
                if (garden != null) {
                    loadPlantsByGardenId(gardenId)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Garden not found") }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadPlantsByGardenId(gardenId: String) {
        viewModelScope.launch {
            plantUseCase.getPlantsByGarden(gardenId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { plants ->
                    _uiState.update { it.copy(plants = plants, isLoading = false) }
                }
        }
    }
}