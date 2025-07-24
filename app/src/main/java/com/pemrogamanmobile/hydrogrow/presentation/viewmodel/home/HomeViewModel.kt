package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.home

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.pemrogamanmobile.hydrogrow.domain.usecase.game.GetGameUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.garden.GardenUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.preferences.GetCachedUserUseCase
import com.pemrogamanmobile.hydrogrow.presentation.uistate.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCachedUserUseCase: GetCachedUserUseCase,
    private val gardenUseCase: GardenUseCase,
    private val getGameUseCase: GetGameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    init {
        loadData()
    }

    fun refresh() {
        loadData()
    }

    fun logViewProfile() {
        firebaseAnalytics.logEvent("view_profile", bundleOf("source" to "home_screen"))
    }

    fun logLogin() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundleOf("method" to "profile_click"))
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 1. Ambil data user dari cache
                val user = getCachedUserUseCase().first()
                _uiState.update { it.copy(user = user) }

                // 2. Ambil semua data kebun
                val gardens = gardenUseCase.getAllGardens().first()
                _uiState.update { it.copy(gardens = gardens) }

                // ✅ 3. AMBIL DATA GAME (JUMLAH PANEN)
                val game = getGameUseCase().firstOrNull()
                _uiState.update { it.copy(cupAmount = game?.cup ?: 0) }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Gagal memuat data") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}