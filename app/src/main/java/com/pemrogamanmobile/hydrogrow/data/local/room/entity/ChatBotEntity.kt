package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.*

@Entity(tableName = "chatbot")
data class ChatBotEntity(
    @PrimaryKey val id: String,
    val userOwnerId: String,
    var title: String,
    val conversation: List<ChatMessageEntity>, // Diubah
    val relatedGardenId: String?,
    val createdAt: Long,
    var updatedAt: Long
)