package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.remote.dto.ChatMessageDto
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage

/**
 * Mengubah ChatMessage (domain model) menjadi ChatMessageDto (untuk Firestore).
 */
fun ChatMessage.toDto(): ChatMessageDto = ChatMessageDto(
    role = this.role,
    content = this.content,
    timestamp = this.timestamp
)

/**
 * Mengubah ChatMessageDto (dari Firestore) menjadi ChatMessage (domain model).
 */
fun ChatMessageDto.toDomain(): ChatMessage = ChatMessage(
    role = this.role,
    content = this.content,
    timestamp = this.timestamp
)