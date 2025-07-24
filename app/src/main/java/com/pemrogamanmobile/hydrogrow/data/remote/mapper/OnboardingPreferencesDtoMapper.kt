package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.remote.dto.OnboardingPreferencesDto
import com.pemrogamanmobile.hydrogrow.domain.model.OnboardingPreferences
import javax.inject.Inject

class OnboardingPreferencesDtoMapper @Inject constructor() {

    fun toDomain(dto: OnboardingPreferencesDto): OnboardingPreferences {
        return OnboardingPreferences(
            userId = dto.userId,
            experience = dto.experience,
            timeAvailable = dto.timeAvailable,
            preferredTime = dto.preferredTime
        )
    }

    fun fromDomain(domain: OnboardingPreferences): OnboardingPreferencesDto {
        return OnboardingPreferencesDto(
            userId = domain.userId,
            experience = domain.experience,
            timeAvailable = domain.timeAvailable,
            preferredTime = domain.preferredTime
        )
    }
}