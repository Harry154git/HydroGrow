package com.pemrogamanmobile.hydrogrow.domain.usecase

import com.pemrogamanmobile.hydrogrow.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    val onboardingCompleted: Flow<Boolean> = repository.onboardingCompleted
    suspend fun setOnboardingCompleted(completed: Boolean) = repository.setOnboardingCompleted(completed)
}
