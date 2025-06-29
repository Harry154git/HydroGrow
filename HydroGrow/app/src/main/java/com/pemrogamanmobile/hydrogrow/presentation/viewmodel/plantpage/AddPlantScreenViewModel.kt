package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.GardenUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.PlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.pemrogamanmobile.hydrogrow.presentation.ui.uistate.AddPlantUiState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import com.pemrogamanmobile.hydrogrow.domain.usecase.UserUseCase
import kotlinx.coroutines.launch
import java.util.UUID
import android.util.Log

@HiltViewModel
class AddPlantViewModel @Inject constructor(
    private val plantUseCase: PlantUseCase,
    private val gardenUseCase: GardenUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {

    var uiState by mutableStateOf(AddPlantUiState())
        private set

    init {
        getAllGardens()
    }

    private fun getAllGardens() {
        viewModelScope.launch {
            val userId = userUseCase.getCurrentUserId()
            if (!userId.isNullOrEmpty()) {
                gardenUseCase.getGardensByUserId(userId)
                    .collect { gardens ->
                        // Update availableGardens
                        uiState = uiState.copy(
                            availableGardens = gardens
                        )

                        // Kalau belum ada garden yang terpilih, set default
                        if (gardens.isNotEmpty() && uiState.selectedGardenId == null) {
                            uiState = uiState.copy(
                                selectedGardenId = gardens.first().id
                            )
                        }
                        Log.d("AddPlantVM", "Gardens loaded: $gardens")
                    }
            } else {
                uiState = uiState.copy(availableGardens = emptyList())
            }
        }
    }

    fun onPlantNameChange(name: String) {
        uiState = uiState.copy(plantName = name)
    }

    fun onNutrientsChange(nutrients: String) {
        if (!uiState.nutrientLocked) {
            uiState = uiState.copy(nutrientsUsed = nutrients)
        }
    }

    fun onHarvestTimeChange(time: String) {
        uiState = uiState.copy(harvestTime = time)
    }

    fun onGardenSelected(gardenId: String) {
        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            try {
                plantUseCase.getPlantsByGarden(gardenId).collect { plantsInGarden ->
                    val existingNutrient = plantsInGarden.firstOrNull()?.nutrientsUsed

                    if (!existingNutrient.isNullOrEmpty()) {
                        // Kalau sudah ada tanaman: nutrient dikunci
                        uiState = uiState.copy(
                            selectedGardenId = gardenId,
                            nutrientsUsed = existingNutrient,
                            nutrientLocked = true,
                            isLoading = false
                        )
                    } else {
                        // Kalau garden kosong: nutrient bebas
                        uiState = uiState.copy(
                            selectedGardenId = gardenId,
                            nutrientsUsed = "",
                            nutrientLocked = false,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    errorMessage = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun savePlant(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (uiState.plantName.isBlank() || uiState.nutrientsUsed.isBlank() || uiState.harvestTime.isBlank() || uiState.selectedGardenId.isBlank()) {
            onError("Semua field wajib diisi.")
            return
        }

        val newPlant = Plant(
            id = UUID.randomUUID().toString(),
            plantName = uiState.plantName,
            nutrientsUsed = uiState.nutrientsUsed,
            harvestTime = uiState.harvestTime,
            gardenOwnerId = uiState.selectedGardenId
        )

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)
                plantUseCase.insertPlant(newPlant)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Gagal menyimpan tanaman")
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}
