package com.pemrogamanmobile.hydrogrow.domain.usecase.onboarding

import com.pemrogamanmobile.hydrogrow.domain.model.OnboardingPreferences
import com.pemrogamanmobile.hydrogrow.domain.repository.OnboardingRepository
import javax.inject.Inject

class SaveOnboardingPreferencesUseCase @Inject constructor(
    private val repository: OnboardingRepository
) {
    suspend operator fun invoke(preferences: OnboardingPreferences) {
        repository.saveOnboardingPreferences(preferences)
    }
}