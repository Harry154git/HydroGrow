package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.model.Garden // Import model domain
import com.pemrogamanmobile.hydrogrow.domain.usecase.garden.GardenUseCase
import com.pemrogamanmobile.hydrogrow.presentation.uistate.GardenUiState
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
                    // Hapus .toUi(), langsung gunakan objek domain
                    garden = garden,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    // Ubah parameter dari GardenUi menjadi Garden
    fun updateGarden(garden: Garden) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // Hapus .toDomain(), langsung oper objek domain
                gardenUseCase.updateGarden(garden)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    // Ubah parameter dari GardenUi menjadi Garden
    fun deleteGarden(garden: Garden) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // Hapus .toDomain(), langsung oper objek domain
                gardenUseCase.deleteGarden(garden)
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