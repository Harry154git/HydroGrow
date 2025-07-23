package com.pemrogamanmobile.hydrogrow.domain.model

data class ChatMessage(
    val role: String, // Hanya "user" atau "model"
    val content: String, // Selalu berisi teks pesan
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: String? = null // FIX: Properti baru untuk URL gambar (opsional)
) {
    companion object {
        const val ROLE_USER = "user"
        const val ROLE_MODEL = "model"
        // ROLE_IMAGE dihapus karena tidak diperlukan lagi
    }
}