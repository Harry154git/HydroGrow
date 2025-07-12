package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.ChatBotEntity
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot

/**
 * Converts a ChatBotEntity (from the database) to a ChatBot (domain model).
 */
fun ChatBotEntity.toDomain(): ChatBot = ChatBot(
    id = this.id,
    userOwnerId = this.userOwnerId,
    conversation = this.conversation.split("|||").filter { it.isNotBlank() },
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Converts a ChatBot (domain model) to a ChatBotEntity (for the database).
 */
fun ChatBot.toEntity(): ChatBotEntity = ChatBotEntity(
    id = this.id,
    userOwnerId = this.userOwnerId,
    conversation = this.conversation.joinToString("|||"),
    createdAt = this.createdAt, // Preserves the original creation timestamp
    updatedAt = System.currentTimeMillis() // Sets the update timestamp to now
)

/**
 * Converts a list of ChatBotEntity objects to a list of ChatBot domain models.
 */
fun List<ChatBotEntity>.toDomainList(): List<ChatBot> = map { it.toDomain() }

/**
 * Converts a list of ChatBot domain models to a list of ChatBotEntity objects.
 */
fun List<ChatBot>.toEntityList(): List<ChatBotEntity> = map { it.toEntity() }
