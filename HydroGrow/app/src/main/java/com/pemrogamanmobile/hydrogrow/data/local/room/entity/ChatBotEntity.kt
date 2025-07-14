package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chatbot")
data class ChatBotEntity(
    @PrimaryKey val id: String,
    val userOwnerId: String,
    val title: String,
    val conversation: String, // List<String> akan diubah menjadi satu String JSON
    val relatedGardenId: String?,
    val createdAt: Long,
    val updatedAt: Long
)