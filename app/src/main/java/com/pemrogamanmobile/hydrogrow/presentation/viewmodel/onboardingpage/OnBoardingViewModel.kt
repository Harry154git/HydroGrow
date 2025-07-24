package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.onboardingpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.model.OnboardingPreferences
import com.pemrogamanmobile.hydrogrow.domain.usecase.auth.GetCurrentUserUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.onboarding.SaveOnboardingPreferencesUseCase
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
    private val saveOnboardingStateUseCase: SaveOnboardingStateUseCase,
    private val saveOnboardingPreferencesUseCase: SaveOnboardingPreferencesUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    // Mengganti OnboardingUserData dengan OnboardingPreferences
    private val _userData = MutableStateFlow(OnboardingPreferences())
    val userData = _userData.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.SaveAndNavigate -> {
                val currentUser = getCurrentUserUseCase()
                val userId = currentUser?.uid

                if (userId == null) {
                    return
                }

                viewModelScope.launch {
                    // Salin state saat ini dan tambahkan userId yang sudah didapat
                    val finalPreferences = _userData.value.copy(userId = userId)

                    // Panggil use case dengan data yang sudah lengkap
                    saveOnboardingPreferencesUseCase(finalPreferences)

                    saveOnboardingStateUseCase(isCompleted = true)
                    _eventFlow.emit(UiEvent.NavigateToHome)
                }
            }
            is OnboardingEvent.UpdateExperience -> {
                // Logika update tetap sama, karena .copy() bekerja dengan baik
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

    sealed class OnboardingEvent {
        data object SaveAndNavigate : OnboardingEvent()
        data class UpdateExperience(val experience: String) : OnboardingEvent()
        data class UpdateTimeAvailable(val time: String) : OnboardingEvent()
        data class UpdatePreferredTime(val time: String) : OnboardingEvent()
    }

    sealed class UiEvent {
        data object NavigateToHome : UiEvent()
    }
}