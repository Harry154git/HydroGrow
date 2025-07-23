package com.pemrogamanmobile.hydrogrow.data.local.room.entity

data class ChatMessageEntity(
    val role: String,
    val content: String,
    val timestamp: Long,
    val imageUrl: String? = null // FIX: Tambahkan properti ini agar cocok dengan model
)