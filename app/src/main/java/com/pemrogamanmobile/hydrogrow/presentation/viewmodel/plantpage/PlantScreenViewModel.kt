package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import com.pemrogamanmobile.hydrogrow.domain.usecase.plant.PlantUseCase
import com.pemrogamanmobile.hydrogrow.presentation.uistate.PlantUiState
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
                    _uiState.value = PlantUiState.Success(plant)
                } else {
                    _uiState.value = PlantUiState.Error("Tanaman tidak ditemukan")
                }
            }
        }
    }

    /**
     * ✅ Fungsi baru untuk menjalankan use case konfirmasi panen.
     * Setelah berhasil, akan kembali ke layar sebelumnya.
     */
    fun harvestPlant(plant: Plant, navController: NavController) {
        viewModelScope.launch {
            // Panggil use case untuk mengkonfirmasi panen
            plantUseCase.confirmHarvest(plant, plant.gardenOwnerId)
            // Kembali ke halaman sebelumnya setelah panen
            navController.popBackStack()
        }
    }
}