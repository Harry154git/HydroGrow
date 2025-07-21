package com.pemrogamanmobile.hydrogrow.data.remote.dto

data class ChatBotDto(
    val id: String = "",
    val userOwnerId: String = "",
    var title: String = "",
    val conversation: List<ChatMessageDto> = emptyList(),
    val relatedGardenId: String? = null,
    val createdAt: Long = 0,
    var updatedAt: Long = 0
)