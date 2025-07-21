package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.remote.dto.ChatBotDto
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot

/**
 * Mengubah ChatBotDto (dari Firestore) menjadi ChatBot (domain model).
 */
fun ChatBotDto.toDomain(): ChatBot = ChatBot(
    id = this.id,
    userOwnerId = this.userOwnerId,
    title = this.title,
    // Petakan setiap ChatMessageDto menjadi ChatMessage
    conversation = this.conversation.map { it.toDomain() }.toMutableList(),
    relatedGardenId = this.relatedGardenId,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Mengubah ChatBot (domain model) menjadi ChatBotDto (untuk Firestore).
 */
fun ChatBot.toDto(): ChatBotDto = ChatBotDto(
    id = this.id,
    userOwnerId = this.userOwnerId,
    title = this.title,
    // Petakan setiap ChatMessage menjadi ChatMessageDto
    conversation = this.conversation.map { it.toDto() },
    relatedGardenId = this.relatedGardenId,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Mengubah list ChatBotDto menjadi list ChatBot domain model.
 */
fun List<ChatBotDto>.toDomainList(): List<ChatBot> = map { it.toDomain() }

/**
 * Mengubah list ChatBot domain model menjadi list ChatBotDto.
 */
fun List<ChatBot>.toDtoList(): List<ChatBotDto> = map { it.toDto() }