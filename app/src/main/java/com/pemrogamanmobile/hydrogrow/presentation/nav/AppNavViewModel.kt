package com.pemrogamanmobile.hydrogrow.presentation.nav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.model.User
import com.pemrogamanmobile.hydrogrow.domain.usecase.auth.GetAuthStateFlowUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.preferences.GetOnboardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppNavViewModel @Inject constructor(
    getOnboardingStateUseCase: GetOnboardingStateUseCase,
    getAuthStateFlowUseCase: GetAuthStateFlowUseCase
) : ViewModel() {

    data class AppState(
        val isLoading: Boolean = true,
        val isOnboardingCompleted: Boolean = false,
        val isSignedIn: Boolean = false
    )

    val appState: StateFlow<AppState> = combine(
        getOnboardingStateUseCase(),
        getAuthStateFlowUseCase()
    ) { onboardingCompleted: Boolean, currentUser: User? ->
        AppState(
            isLoading = false,
            isOnboardingCompleted = onboardingCompleted,
            isSignedIn = currentUser != null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppState(isLoading = true)
    )
}