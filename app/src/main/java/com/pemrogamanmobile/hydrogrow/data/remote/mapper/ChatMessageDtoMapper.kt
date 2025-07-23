package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.remote.dto.ChatMessageDto // Asumsi DTO ada di sini
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage

fun ChatMessage.toDto(): ChatMessageDto = ChatMessageDto(
    role = this.role,
    content = this.content,
    timestamp = this.timestamp,
    imageUrl = this.imageUrl // FIX: Map properti baru
)

fun ChatMessageDto.toDomain(): ChatMessage = ChatMessage(
    role = this.role,
    content = this.content,
    timestamp = this.timestamp,
    imageUrl = this.imageUrl // FIX: Map properti baru
)