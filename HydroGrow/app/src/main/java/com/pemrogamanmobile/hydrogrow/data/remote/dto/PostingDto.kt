package com.pemrogamanmobile.hydrogrow.data.remote.dto

import com.google.firebase.firestore.PropertyName

/**
 * DTO untuk Posting. Strukturnya tetap sama.
 */
data class PostingDto(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("user_owner_id")
    @set:PropertyName("user_owner_id")
    var userOwnerId: String = "",

    @get:PropertyName("user_owner_name")
    @set:PropertyName("user_owner_name")
    var userOwnerName: String = "",

    @get:PropertyName("user_owner_profile_url")
    @set:PropertyName("user_owner_profile_url")
    var userOwnerProfileUrl: String? = null,

    @get:PropertyName("image_url")
    @set:PropertyName("image_url")
    var imageUrl: String? = null,

    @get:PropertyName("likes")
    @set:PropertyName("likes")
    var likes: String = "0",

    @get:PropertyName("comments")
    @set:PropertyName("comments")
    var comments: List<CommentDto> = emptyList(),

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Long = 0L,

    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: Long = 0L
)