package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.GardenUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.PlantUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.UserUseCase
import com.pemrogamanmobile.hydrogrow.presentation.mapper.toUi
import com.pemrogamanmobile.hydrogrow.presentation.model.GardenUi
import com.pemrogamanmobile.hydrogrow.presentation.model.PlantUi
import com.pemrogamanmobile.hydrogrow.presentation.ui.uistate.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.coroutineScope

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val gardenUseCase: GardenUseCase,
    private val plantUseCase: PlantUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun refresh() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val userId = userUseCase.getCurrentUserId()
            android.util.Log.d("HomeVM", "UserId: $userId")

            if (userId.isNullOrEmpty()) {
                _uiState.update { it.copy(error = "User not logged in") }
                return@launch
            }

            // Load user profile
            launch {
                userUseCase.getProfile()
                    .map { user ->
                        android.util.Log.d("HomeVM", "User profile raw: $user")
                        user?.toUi()
                    }
                    .catch { e ->
                        android.util.Log.e("HomeVM", "Error getProfile: ${e.message}")
                        _uiState.update { it.copy(error = e.message ?: "Failed to load user") }
                    }
                    .collect { userUi ->
                        android.util.Log.d("HomeVM", "Mapped userUi: $userUi")
                        _uiState.update { it.copy(user = userUi) }
                    }
            }

            // Load gardens
            gardenUseCase.getGardensByUserId(userId)
                .map { gardens ->
                    android.util.Log.d("HomeVM", "Gardens raw: $gardens")
                    gardens.map { it.toUi() }
                }
                .catch { e ->
                    android.util.Log.e("HomeVM", "Error getGardens: ${e.message}")
                    _uiState.update { it.copy(error = e.message ?: "Failed to load gardens") }
                }
                .collect { gardenUiList ->
                    android.util.Log.d("HomeVM", "Mapped gardenUiList: $gardenUiList")
                    _uiState.update { it.copy(gardens = gardenUiList) }
                    loadPlantsForGardens(gardenUiList)
                }
        }
    }

    private suspend fun loadPlantsForGardens(gardenUiList: List<GardenUi>) {
        val plantsPerGarden = mutableMapOf<String, List<PlantUi>>()
        val mutex = kotlinx.coroutines.sync.Mutex()

        coroutineScope {
            gardenUiList.forEach { gardenUi ->
                launch {
                    plantUseCase.getPlantsByGarden(gardenUi.id)
                        .map { list -> list.map { it.toUi() } }
                        .catch { e ->
                            _uiState.update { it.copy(error = e.message ?: "Failed to load plants for ${gardenUi.name}") }
                        }
                        .collect { plantUiList ->
                            mutex.withLock {
                                plantsPerGarden[gardenUi.id] = plantUiList
                                // Gabungkan semua plants dari semua kebun
                                val combined = plantsPerGarden.values.flatten()
                                _uiState.update { it.copy(plants = combined) }
                            }
                        }
                }
            }
        }
    }
}