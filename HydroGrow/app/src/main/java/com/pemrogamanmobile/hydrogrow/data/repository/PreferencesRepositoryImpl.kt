package com.pemrogamanmobile.hydrogrow.data.repository

import com.pemrogamanmobile.hydrogrow.data.local.datastore.PreferenceManager
import com.pemrogamanmobile.hydrogrow.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager
) : PreferencesRepository {
    override fun observeDarkMode(): Flow<Boolean> = preferenceManager.darkModeFlow
    override suspend fun setDarkMode(enabled: Boolean) = preferenceManager.setDarkMode(enabled)
    override fun observeLanguage(): Flow<String> = preferenceManager.languageFlow
    override suspend fun setLanguage(lang: String) = preferenceManager.setLanguage(lang)

    override val onboardingCompleted: Flow<Boolean>
        get() = preferenceManager.onboardingCompletedFlow

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        preferenceManager.setOnboardingCompleted(completed)
    }
}
