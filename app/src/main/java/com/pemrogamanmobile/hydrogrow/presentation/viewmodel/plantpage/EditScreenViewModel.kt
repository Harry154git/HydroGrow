package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.plant.PlantUseCase
import com.pemrogamanmobile.hydrogrow.presentation.uistate.EditPlantUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPlantViewModel @Inject constructor(
    private val plantUseCase: PlantUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(EditPlantUiState())
        private set

    fun loadPlant(plantId: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            try {
                plantUseCase.getPlantById(plantId).collect { plant ->
                    plant?.let {
                        uiState = uiState.copy(
                            originalPlant = it,
                            plantName = it.plantName,
                            harvestTime = it.harvestTime,
                            cupAmount = it.cupAmount.toString(),
                            isLoading = false
                        )
                    } ?: run {
                        uiState = uiState.copy(isLoading = false, errorMessage = "Tanaman tidak ditemukan")
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun onPlantNameChange(newName: String) {
        uiState = uiState.copy(plantName = newName)
    }

    fun onHarvestTimeChange(newTime: String) {
        uiState = uiState.copy(harvestTime = newTime)
    }

    fun onCupAmountChange(amount: String) {
        if (amount.all { it.isDigit() }) {
            uiState = uiState.copy(cupAmount = amount)
        }
    }

    fun updatePlant() {
        val originalPlant = uiState.originalPlant ?: return

        viewModelScope.launch {
            try {
                val updatedPlant = originalPlant.copy(
                    plantName = uiState.plantName,
                    harvestTime = uiState.harvestTime,
                    cupAmount = uiState.cupAmount.toIntOrNull() ?: originalPlant.cupAmount
                )

                plantUseCase.updatePlant(updatedPlant)
                uiState = uiState.copy(isSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }

    fun deletePlant() {
        val plantToDelete = uiState.originalPlant ?: return

        viewModelScope.launch {
            try {
                // ✅ DIPERBARUI: Tambahkan argumen gardenId yang diambil dari objek plant itu sendiri
                plantUseCase.deletePlant(
                    plant = plantToDelete,
                    gardenId = plantToDelete.gardenOwnerId
                )
                uiState = uiState.copy(isSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }
}