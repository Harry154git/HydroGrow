package com.pemrogamanmobile.hydrogrow.data.repository

import com.pemrogamanmobile.hydrogrow.data.local.datastore.PreferenceManager
import com.pemrogamanmobile.hydrogrow.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager
) : PreferencesRepository {

    override val onboardingCompleted: Flow<Boolean>
        get() = preferenceManager.onboardingCompletedFlow

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        preferenceManager.setOnboardingCompleted(completed)
    }
}
