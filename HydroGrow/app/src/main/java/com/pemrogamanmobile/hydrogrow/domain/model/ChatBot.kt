package com.pemrogamanmobile.hydrogrow.domain.model

data class ChatBot(
    val id: String,
    val userOwnerId: String,
    var title: String = "Percakapan Baru", // Ditambahkan untuk judul histori
    val conversation: MutableList<String>, // Diubah menjadi MutableList agar mudah ditambah
    val relatedGardenId: String? = null, // Ditambahkan untuk chat berkonteks
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)