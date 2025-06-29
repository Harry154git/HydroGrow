package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.GardenUseCase
import com.pemrogamanmobile.hydrogrow.presentation.model.GardenUi
import com.pemrogamanmobile.hydrogrow.presentation.ui.uistate.GardenUiState
import com.pemrogamanmobile.hydrogrow.presentation.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.presentation.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EditGardenViewModel @Inject constructor(
    private val gardenUseCase: GardenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GardenUiState())
    val uiState: StateFlow<GardenUiState> = _uiState

    fun loadGarden(gardenId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val garden = gardenUseCase.getGardenById(gardenId)
                _uiState.value = _uiState.value.copy(
                    garden = garden?.toUi(),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun updateGarden(gardenUi: GardenUi) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                gardenUseCase.updateGarden(gardenUi.toDomain())
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun deleteGarden(gardenUi: GardenUi) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                gardenUseCase.deleteGarden(gardenUi.toDomain())
                _uiState.value = _uiState.value.copy(garden = null, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun resetError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}