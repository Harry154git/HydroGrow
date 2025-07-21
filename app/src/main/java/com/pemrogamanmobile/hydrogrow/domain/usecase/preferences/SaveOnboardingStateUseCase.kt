package com.pemrogamanmobile.hydrogrow.domain.usecase.preferences

import com.pemrogamanmobile.hydrogrow.domain.repository.PreferencesRepository
import javax.inject.Inject

class SaveOnboardingStateUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke(isCompleted: Boolean) {
        repository.setOnboardingCompleted(isCompleted)
    }
}