package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.onboardingpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.model.OnboardingUserData
import com.pemrogamanmobile.hydrogrow.domain.usecase.preferences.SaveOnboardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingScreenViewModel @Inject constructor(
    private val saveOnboardingStateUseCase: SaveOnboardingStateUseCase
) : ViewModel() {

    private val _userData = MutableStateFlow(OnboardingUserData())
    val userData = _userData.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: OnboardingEvent) {
        when(event) {
            is OnboardingEvent.SaveAndNavigate -> {
                viewModelScope.launch {
                    // Menyimpan state bahwa onboarding sudah selesai
                    saveOnboardingStateUseCase(isCompleted = true)
                    // TODO: Anda bisa menyimpan _userData.value ke repository jika diperlukan di masa depan
                    // Mengirim event ke UI untuk melakukan navigasi ke halaman AI
                    _eventFlow.emit(UiEvent.NavigateToCreateGardenAi)
                }
            }
            is OnboardingEvent.UpdateExperience -> {
                _userData.update { it.copy(experience = event.experience) }
            }
            is OnboardingEvent.UpdateTimeAvailable -> {
                _userData.update { it.copy(timeAvailable = event.time) }
            }
            is OnboardingEvent.UpdatePreferredTime -> {
                _userData.update { it.copy(preferredTime = event.time) }
            }
        }
    }

    // Sealed class untuk mendefinisikan event dari UI
    sealed class OnboardingEvent {
        data object SaveAndNavigate : OnboardingEvent()
        data class UpdateExperience(val experience: String) : OnboardingEvent()
        data class UpdateTimeAvailable(val time: String) : OnboardingEvent()
        data class UpdatePreferredTime(val time: String) : OnboardingEvent()
    }

    // Sealed class untuk mendefinisikan event yang dikirim ke UI
    sealed class UiEvent {
        data object NavigateToCreateGardenAi : UiEvent()
    }
}