package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.remote.dto.ChatBotDto
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot

/**
 * Converts a ChatBotDto (from Firestore) to a ChatBot (domain model).
 */
fun ChatBotDto.toDomain(): ChatBot = ChatBot(
    id = this.id,
    userOwnerId = this.userOwnerId,
    conversation = this.conversation, // Direct mapping, no need to split
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Converts a ChatBot (domain model) to a ChatBotDto (for Firestore).
 */
fun ChatBot.toDto(): ChatBotDto = ChatBotDto(
    id = this.id,
    userOwnerId = this.userOwnerId,
    conversation = this.conversation, // Direct mapping, no need to join
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Converts a list of ChatBotDto objects to a list of ChatBot domain models.
 */
fun List<ChatBotDto>.toDomainList(): List<ChatBot> = map { it.toDomain() }

/**
 * Converts a list of ChatBot domain models to a list of ChatBotDto objects.
 */
fun List<ChatBot>.toDtoList(): List<ChatBotDto> = map { it.toDto() }