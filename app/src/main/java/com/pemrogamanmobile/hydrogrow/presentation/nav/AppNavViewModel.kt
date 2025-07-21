package com.pemrogamanmobile.hydrogrow.presentation.nav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.preferences.GetOnboardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppNavViewModel @Inject constructor(
    // Menggunakan GetOnboardingStateUseCase untuk mengambil data dari repository
    getOnboardingStateUseCase: GetOnboardingStateUseCase
) : ViewModel() {

    /**
     * StateFlow yang memancarkan status penyelesaian onboarding.
     * Nilai awalnya adalah `null` untuk menandakan status loading saat data
     * pertama kali diambil dari DataStore.
     * UI akan menampilkan loading indicator selama nilainya masih null.
     */
    val onboardingCompleted: StateFlow<Boolean?> = getOnboardingStateUseCase()
        .stateIn(
            scope = viewModelScope, // Scope coroutine yang terikat dengan siklus hidup ViewModel
            started = SharingStarted.WhileSubscribed(5_000), // Flow tetap aktif 5 detik setelah collector terakhir berhenti
            initialValue = null // Nilai awal adalah null untuk menangani state loading
        )
}