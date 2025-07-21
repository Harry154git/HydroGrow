package com.pemrogamanmobile.hydrogrow.domain.model

data class ChatBot(
    val id: String,
    val userOwnerId: String,
    var title: String = "Percakapan Baru",
    val conversation: MutableList<ChatMessage>, // Diubah ke MutableList<ChatMessage>
    val relatedGardenId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)