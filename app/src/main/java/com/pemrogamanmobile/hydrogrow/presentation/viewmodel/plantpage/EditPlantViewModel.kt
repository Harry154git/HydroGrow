package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage

import android.net.Uri
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
    private val plantUseCase: PlantUseCase
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
                            isLoading = false,
                            newImageUri = null // Reset URI saat memuat data baru
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

    // ✅ DITAMBAHKAN: Fungsi untuk menerima URI gambar baru dari UI
    fun onImageSelected(uri: Uri) {
        uiState = uiState.copy(newImageUri = uri)
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
                // ✅ DIPERBARUI: Logika untuk mengunggah gambar baru jika ada
                val imageUrl = if (uiState.newImageUri != null) {
                    plantUseCase.uploadPlantImage(uiState.newImageUri!!)
                } else {
                    originalPlant.imageUrl // Gunakan URL lama jika tidak ada gambar baru
                }

                val updatedPlant = originalPlant.copy(
                    plantName = uiState.plantName,
                    harvestTime = uiState.harvestTime,
                    cupAmount = uiState.cupAmount.toIntOrNull() ?: originalPlant.cupAmount,
                    imageUrl = imageUrl // Set dengan URL baru atau lama
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