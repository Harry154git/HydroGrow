package com.pemrogamanmobile.hydrogrow.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.OnboardingPreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OnboardingPreferencesDao {
    @Upsert
    suspend fun upsertPreferences(preferences: OnboardingPreferencesEntity)

    @Query("SELECT * FROM onboarding_preferences WHERE userId = :userId")
    fun getPreferences(userId: String): Flow<OnboardingPreferencesEntity?>

    @Query("DELETE FROM onboarding_preferences WHERE userId = :userId")
    suspend fun deletePreferences(userId: String)
}