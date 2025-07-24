package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "onboarding_preferences")
data class OnboardingPreferencesEntity(
    @PrimaryKey
    val userId: String,
    val experience: String,
    val timeAvailable: String,
    val preferredTime: String
)