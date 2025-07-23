package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.onboardingpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.preferences.SaveOnboardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingScreenViewModel @Inject constructor(
    // 1. Inject use case yang dibutuhkan
    private val saveOnboardingStateUseCase: SaveOnboardingStateUseCase
) : ViewModel() {

    // 2. Gunakan SharedFlow untuk event sekali jalan seperti navigasi
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    /**
     * Fungsi ini dipanggil saat tombol "Skip" atau "Selanjutnya" ditekan.
     * Ia akan menyimpan status onboarding dan kemudian mengirim event untuk navigasi.
     */
    fun onEvent(event: OnboardingEvent) {
        when(event) {
            is OnboardingEvent.SaveAndNavigate -> {
                viewModelScope.launch {
                    // Menyimpan state bahwa onboarding sudah selesai
                    saveOnboardingStateUseCase(isCompleted = true)
                    // Mengirim event ke UI untuk melakukan navigasi
                    _eventFlow.emit(UiEvent.NavigateToLogin)
                }
            }
        }
    }

    // Sealed class untuk mendefinisikan event dari UI
    sealed class OnboardingEvent {
        data object SaveAndNavigate : OnboardingEvent()
    }

    // Sealed class untuk mendefinisikan event yang dikirim ke UI
    sealed class UiEvent {
        data object NavigateToLogin : UiEvent()
    }
}