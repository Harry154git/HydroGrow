package com.pemrogamanmobile.hydrogrow.domain.model

data class GardenCreationParams(
    val kondisiCahaya: String,
    val panjangLahan: Float?, // Nullable jika pengguna minta rekomendasi
    val lebarLahan: Float?,   // Nullable jika pengguna minta rekomendasi
    val mintaRekomendasiLahan: Boolean,
    val suhuCuaca: String,
    val jenisTanaman: String,
    val mintaRekomendasiTanaman: Boolean,
    val tujuanSkala: String,
    val rentangBiaya: String
)