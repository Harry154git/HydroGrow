package com.pemrogamanmobile.hydrogrow.domain.model

data class ChatMessage(
    val role: String, // "user", "model", atau "image"
    val content: String, // Teks pesan atau URL gambar
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        const val ROLE_USER = "user"
        const val ROLE_MODEL = "model"
        const val ROLE_IMAGE = "image" // Peran khusus untuk pesan gambar
    }
}