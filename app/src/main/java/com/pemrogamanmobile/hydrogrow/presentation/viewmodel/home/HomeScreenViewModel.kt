package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.garden.GardenUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.plant.PlantUseCase
import com.pemrogamanmobile.hydrogrow.presentation.mapper.toUi
import com.pemrogamanmobile.hydrogrow.presentation.model.GardenUi
import com.pemrogamanmobile.hydrogrow.presentation.model.PlantUi
import com.pemrogamanmobile.hydrogrow.presentation.ui.uistate.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.coroutineScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import androidx.core.os.bundleOf
import com.pemrogamanmobile.hydrogrow.domain.usecase.auth.GetCurrentUserUseCase

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authUseCase: GetCurrentUserUseCase,
    private val gardenUseCase: GardenUseCase,
    private val plantUseCase: PlantUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val firebaseAnalytics = Firebase.analytics

    init {
        loadData()
    }

    fun refresh() {
        loadData()
    }

    fun logViewProfile() {
        firebaseAnalytics.logEvent("view_profile", bundleOf(
            "source" to "home_screen"
        ))
    }

    fun logLogin() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundleOf(
            "method" to "profile_click"
        ))
    }

    private fun loadData() {
        // nanti ya
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