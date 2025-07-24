package com.pemrogamanmobile.hydrogrow.domain.model

/**
 * Model data utama yang sekarang juga digunakan untuk menampung state sementara
 * di ViewModel. Nilai default ditambahkan untuk memungkinkan inisialisasi kosong.
 */
data class OnboardingPreferences(
    val userId: String = "",
    val experience: String = "",
    val timeAvailable: String = "",
    val preferredTime: String = ""
)