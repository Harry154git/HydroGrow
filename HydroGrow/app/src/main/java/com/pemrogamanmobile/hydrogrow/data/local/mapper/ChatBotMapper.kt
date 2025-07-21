package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.ChatBotEntity
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot

/**
 * Mengubah ChatBotEntity (dari database) menjadi ChatBot (domain model).
 */
fun ChatBotEntity.toDomain(): ChatBot = ChatBot(
    id = this.id,
    userOwnerId = this.userOwnerId,
    title = this.title,
    // Ubah setiap item di List<ChatMessageEntity> menjadi ChatMessage
    conversation = this.conversation.map { it.toDomain() }.toMutableList(),
    relatedGardenId = this.relatedGardenId,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Mengubah ChatBot (domain model) menjadi ChatBotEntity (untuk database).
 */
fun ChatBot.toEntity(): ChatBotEntity = ChatBotEntity(
    id = this.id,
    userOwnerId = this.userOwnerId,
    title = this.title,
    // Ubah setiap item di List<ChatMessage> menjadi ChatMessageEntity
    conversation = this.conversation.map { it.toEntity() },
    relatedGardenId = this.relatedGardenId,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Mengubah list ChatBotEntity menjadi list ChatBot domain model.
 */
fun List<ChatBotEntity>.toDomainList(): List<ChatBot> = map { it.toDomain() }

/**
 * Mengubah list ChatBot domain model menjadi list ChatBotEntity.
 */
fun List<ChatBot>.toEntityList(): List<ChatBotEntity> = map { it.toEntity() }