package com.pemrogamanmobile.hydrogrow.domain.model

/**
 * Data class untuk menampung informasi yang dikumpulkan dari pengguna
 * selama proses onboarding.
 */
data class OnboardingUserData(
    val experience: String = "",
    val timeAvailable: String = "",
    val preferredTime: String = ""
)