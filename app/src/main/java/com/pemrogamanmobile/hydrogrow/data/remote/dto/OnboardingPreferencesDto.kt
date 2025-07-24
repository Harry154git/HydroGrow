package com.pemrogamanmobile.hydrogrow.data.remote.dto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class OnboardingPreferencesDto(
    val userId: String = "",
    val experience: String = "",
    val timeAvailable: String = "",
    val preferredTime: String = "",
    @ServerTimestamp
    val timestamp: Date? = null
)