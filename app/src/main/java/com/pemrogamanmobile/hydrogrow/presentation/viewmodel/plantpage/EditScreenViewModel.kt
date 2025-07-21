package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import com.pemrogamanmobile.hydrogrow.domain.usecase.plant.PlantUseCase
import com.pemrogamanmobile.hydrogrow.data.repository.GardenUseCase
import com.pemrogamanmobile.hydrogrow.presentation.ui.uistate.EditPlantUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPlantViewModel @Inject constructor(
    private val plantUseCase: PlantUseCase,
    private val gardenUseCase: GardenUseCase
) : ViewModel() {

    var uiState by mutableStateOf(EditPlantUiState())
        private set

    fun loadPlant(plantId: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            try {
                plantUseCase.getPlantById(plantId).collect { plant ->
                    plant?.let {
                        val gardens = gardenUseCase.getAllGardens().first()
                        val nutrients = plantUseCase.getPlantsByGarden(it.gardenOwnerId)
                            .first()
                            .mapNotNull { p -> p.nutrientsUsed }
                            .distinct()

                        uiState = uiState.copy(
                            plantId = it.id,
                            gardenOwnerId = it.gardenOwnerId,
                            availableGardens = gardens,
                            availableNutrients = nutrients,
                            plantName = it.plantName,
                            nutrientsUsed = it.nutrientsUsed,
                            harvestTime = it.harvestTime,
                            isLoading = false
                        )
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

    fun onNutrientsUsedChange(newNutrients: String) {
        uiState = uiState.copy(nutrientsUsed = newNutrients)
    }

    fun onGardenChange(newGardenId: String) {
        uiState = uiState.copy(gardenOwnerId = newGardenId)
        // reload available nutrients ketika garden diganti
        viewModelScope.launch {
            val nutrients = plantUseCase.getPlantsByGarden(newGardenId)
                .first()
                .mapNotNull { it.nutrientsUsed }
                .distinct()
            uiState = uiState.copy(availableNutrients = nutrients)
        }
    }

    fun onHarvestTimeChange(newTime: String) {
        uiState = uiState.copy(harvestTime = newTime)
    }

    fun updatePlant() {
        viewModelScope.launch {
            try {
                plantUseCase.updatePlant(
                    Plant(
                        id = uiState.plantId,
                        plantName = uiState.plantName,
                        nutrientsUsed = uiState.nutrientsUsed,
                        harvestTime = uiState.harvestTime,
                        gardenOwnerId = uiState.gardenOwnerId
                    )
                )
                uiState = uiState.copy(isSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }

    fun deletePlant() {
        viewModelScope.launch {
            try {
                plantUseCase.deletePlant(
                    Plant(
                        id = uiState.plantId,
                        plantName = uiState.plantName,
                        nutrientsUsed = uiState.nutrientsUsed,
                        harvestTime = uiState.harvestTime,
                        gardenOwnerId = uiState.gardenOwnerId
                    ),
                    gardenId = uiState.gardenOwnerId
                )
                uiState = uiState.copy(isSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }
}