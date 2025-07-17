package com.pemrogamanmobile.hydrogrow.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val onboardingCompleted: Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
}