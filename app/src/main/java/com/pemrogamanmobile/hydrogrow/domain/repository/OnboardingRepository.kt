package com.pemrogamanmobile.hydrogrow.domain.repository

import com.pemrogamanmobile.hydrogrow.domain.model.OnboardingPreferences
import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    fun getOnboardingPreferences(userId: String): Flow<OnboardingPreferences?>
    suspend fun saveOnboardingPreferences(preferences: OnboardingPreferences)
    suspend fun syncPreferences(userId: String)
}