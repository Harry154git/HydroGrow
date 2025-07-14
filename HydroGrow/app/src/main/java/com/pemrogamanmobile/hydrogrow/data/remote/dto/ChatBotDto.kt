package com.pemrogamanmobile.hydrogrow.data.remote.dto

import com.google.firebase.firestore.PropertyName

/**
 * Data Transfer Object (DTO) for ChatBot stored in Firestore.
 * It's crucial for all properties to have default values for Firestore's automatic deserialization.
 */
data class ChatBotDto(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("user_owner_id")
    @set:PropertyName("user_owner_id")
    var userOwnerId: String = "",

    @get:PropertyName("conversation")
    @set:PropertyName("conversation")
    var conversation: List<String> = emptyList(),

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Long = 0L,

    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: Long = 0L
)