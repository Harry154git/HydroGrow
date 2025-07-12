package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chatbot")
data class ChatBotEntity(
    @PrimaryKey val id: String,

    @ColumnInfo(name = "user_owner_id")
    val userOwnerId: String,

    val conversation: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)