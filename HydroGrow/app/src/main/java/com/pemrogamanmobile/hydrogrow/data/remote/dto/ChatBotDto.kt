package com.pemrogamanmobile.hydrogrow.data.remote.dto

import com.google.firebase.firestore.PropertyName

/**
 * DTO untuk menyimpan dan mengambil data ChatBot dari Firestore.
 */
data class ChatBotDto(
    var id: String = "",
    @get:PropertyName("user_owner_id") @set:PropertyName("user_owner_id")
    var userOwnerId: String = "",
    var title: String = "",
    var conversation: List<String> = emptyList(),
    @get:PropertyName("related_garden_id") @set:PropertyName("related_garden_id")
    var relatedGardenId: String? = null,
    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Long = 0L,
    @get:PropertyName("updated_at") @set:PropertyName("updated_at")
    var updatedAt: Long = 0L
)