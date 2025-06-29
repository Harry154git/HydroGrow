package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.onboardingpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingScreenViewModel @Inject constructor(
    private val onboardingRepository: PreferencesRepository
) : ViewModel() {

    val onboardingCompleted: StateFlow<Boolean?> =
        onboardingRepository.onboardingCompleted
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun setOnboardingCompleted() {
        viewModelScope.launch {
            onboardingRepository.setOnboardingCompleted(true)
        }
    }
}