package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.ChatMessageEntity
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage

/**
 * Mengubah ChatMessage (domain model) menjadi ChatMessageEntity (database entity).
 */
fun ChatMessage.toEntity(): ChatMessageEntity = ChatMessageEntity(
    role = this.role,
    content = this.content,
    timestamp = this.timestamp
)

/**
 * Mengubah ChatMessageEntity (database entity) menjadi ChatMessage (domain model).
 */
fun ChatMessageEntity.toDomain(): ChatMessage = ChatMessage(
    role = this.role,
    content = this.content,
    timestamp = this.timestamp
)