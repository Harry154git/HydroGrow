package com.pemrogamanmobile.hydrogrow.domain.model

data class ChatBot(
    val id: String,
    val userOwnerId: String,
    val conversation: List<String>,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)