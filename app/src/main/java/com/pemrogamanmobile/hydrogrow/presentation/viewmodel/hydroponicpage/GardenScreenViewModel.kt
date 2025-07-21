package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.auth.GetCurrentUserUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.garden.GardenUseCase
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
    private val authUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GardenUiState())
    val uiState: StateFlow<GardenUiState> = _uiState.asStateFlow()

    fun loadGardenById(gardenId: String) {
        // nanti ya
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