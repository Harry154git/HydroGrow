package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.data.repository.GardenUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.plant.PlantUseCase
import com.pemrogamanmobile.hydrogrow.presentation.ui.uistate.GardenUiState
import com.pemrogamanmobile.hydrogrow.presentation.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@HiltViewModel
class GardenViewModel @Inject constructor(
    private val gardenUseCase: GardenUseCase,
    private val plantUseCase: PlantUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GardenUiState())
    val uiState: StateFlow<GardenUiState> = _uiState.asStateFlow()

    fun loadGardenById(gardenId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val userId = userUseCase.getCurrentUserId()

            if (userId.isNullOrEmpty()) {
                _uiState.update { it.copy(isLoading = false, error = "User belum login") }
                return@launch
            }

            try {
                val garden = gardenUseCase.getGardenById(gardenId)
                if (garden != null && garden.userOwnerId == userId) {
                    _uiState.update { it.copy(garden = garden.toUi(), isLoading = false) }
                    loadPlantsByGardenId(gardenId)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Garden tidak ditemukan atau bukan milik user ini") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadPlantsByGardenId(gardenId: String) {
        viewModelScope.launch {
            plantUseCase.getPlantsByGarden(gardenId)
                .map { list -> list.map { it.toUi() } }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { plants ->
                    _uiState.update { it.copy(plants = plants, isLoading = false) }
                }
        }
    }
}