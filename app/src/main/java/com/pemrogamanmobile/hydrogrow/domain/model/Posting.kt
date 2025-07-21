package com.pemrogamanmobile.hydrogrow.domain.model

data class Posting(
    val id: String,
    val userOwnerId: String,
    val userOwnerName: String,
    val userOwnerProfileUrl: String?,
    val imageUrl: String?,
    val likes: Int,
    val comments: List<Comment>,
    val createdAt: Long,
    val updatedAt: Long
)