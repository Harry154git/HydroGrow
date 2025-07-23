package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.ChatMessageEntity
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage

fun ChatMessage.toEntity(): ChatMessageEntity = ChatMessageEntity(
    role = this.role,
    content = this.content,
    timestamp = this.timestamp,
    imageUrl = this.imageUrl // FIX: Map properti baru
)

fun ChatMessageEntity.toDomain(): ChatMessage = ChatMessage(
    role = this.role,
    content = this.content,
    timestamp = this.timestamp,
    imageUrl = this.imageUrl // FIX: Map properti baru
)