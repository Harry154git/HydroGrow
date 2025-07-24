package com.pemrogamanmobile.hydrogrow.domain.model

data class GardenCreationParams(
    val kondisiCahaya: String,
    val suhuCuaca: String,
    val tujuanSkala: String,
    val rentangBiaya: String,
    val mintaRekomendasiLahan: Boolean,
    val panjangLahan: Int?,
    val lebarLahan: Int?,
    val mintaRekomendasiTanaman: Boolean,
    val jenisTanaman: String?
)