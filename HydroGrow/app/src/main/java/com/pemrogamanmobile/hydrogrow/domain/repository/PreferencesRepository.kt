package com.pemrogamanmobile.hydrogrow.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val onboardingCompleted: Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)

    fun observeDarkMode(): Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)

    fun observeLanguage(): Flow<String>
    suspend fun setLanguage(lang: String)
}