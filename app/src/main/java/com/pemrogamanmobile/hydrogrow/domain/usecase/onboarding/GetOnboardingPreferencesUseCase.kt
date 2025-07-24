package com.pemrogamanmobile.hydrogrow.domain.usecase.onboarding

import com.pemrogamanmobile.hydrogrow.domain.model.OnboardingPreferences
import com.pemrogamanmobile.hydrogrow.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOnboardingPreferencesUseCase @Inject constructor(
    private val repository: OnboardingRepository
) {
    operator fun invoke(userId: String): Flow<OnboardingPreferences?> {
        return repository.getOnboardingPreferences(userId)
    }
}