package com.pemrogamanmobile.hydrogrow.domain.model

data class Posting(
    val id: String,
    val userOwnerId: String,
    val comment: String,
    val imageUrl: String,
    val likes: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)