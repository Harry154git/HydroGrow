package com.pemrogamanmobile.hydrogrow.domain.usecase

import com.pemrogamanmobile.hydrogrow.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    val onboardingCompleted: Flow<Boolean> = repository.onboardingCompleted
    suspend fun setOnboardingCompleted(completed: Boolean) = repository.setOnboardingCompleted(completed)

    fun observeDarkMode(): Flow<Boolean> = repository.observeDarkMode()
    suspend fun setDarkMode(enabled: Boolean) = repository.setDarkMode(enabled)

    fun observeLanguage(): Flow<String> = repository.observeLanguage()
    suspend fun setLanguage(lang: String) = repository.setLanguage(lang)
}
