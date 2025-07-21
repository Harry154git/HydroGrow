package com.pemrogamanmobile.hydrogrow.domain.usecase.preferences

import com.pemrogamanmobile.hydrogrow.domain.repository.PreferencesRepository
import javax.inject.Inject

class GetOnboardingStateUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    operator fun invoke() = repository.cachedOnboardingState
}