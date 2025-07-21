package com.pemrogamanmobile.hydrogrow.domain.repository

import com.pemrogamanmobile.hydrogrow.domain.model.User
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val cachedOnboardingState: Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
    val cachedUser: Flow<User?>
    suspend fun saveUserToCache(user: User)
    suspend fun clearUserCache()
}