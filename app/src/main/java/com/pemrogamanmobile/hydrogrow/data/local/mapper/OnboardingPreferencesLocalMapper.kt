package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.OnboardingPreferencesEntity
import com.pemrogamanmobile.hydrogrow.domain.model.OnboardingPreferences
import javax.inject.Inject

class OnboardingPreferencesLocalMapper @Inject constructor() {
    fun toDomain(entity: OnboardingPreferencesEntity): OnboardingPreferences {
        return OnboardingPreferences(
            userId = entity.userId,
            experience = entity.experience,
            timeAvailable = entity.timeAvailable,
            preferredTime = entity.preferredTime
        )
    }

    fun fromDomain(domain: OnboardingPreferences): OnboardingPreferencesEntity {
        return OnboardingPreferencesEntity(
            userId = domain.userId,
            experience = domain.experience,
            timeAvailable = domain.timeAvailable,
            preferredTime = domain.preferredTime
        )
    }
}