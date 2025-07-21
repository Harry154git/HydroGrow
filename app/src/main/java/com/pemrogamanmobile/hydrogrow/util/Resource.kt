package com.pemrogamanmobile.hydrogrow.util

/**
 * Sebuah generic sealed class yang digunakan untuk membungkus data
 * yang diambil dari repository, beserta statusnya.
 * @param T Tipe data yang akan dibungkus.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Menandakan data berhasil dimuat.
     * @param data Data yang berhasil didapatkan.
     */
    class Success<T>(data: T) : Resource<T>(data)

    /**
     * Menandakan terjadi error saat memuat data.
     * @param message Pesan error yang bisa ditampilkan ke pengguna.
     * @param data (Opsional) Data lama/stale yang mungkin masih ada.
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /**
     * Menandakan data sedang dalam proses dimuat (loading).
     */
    class Loading<T> : Resource<T>()
}