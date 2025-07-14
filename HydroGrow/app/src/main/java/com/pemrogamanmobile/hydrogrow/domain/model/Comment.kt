package com.pemrogamanmobile.hydrogrow.domain.model

data class Comment(
    val id: String,
    val postId: String,
    val userCommentId: String,
    val userName: String,
    val userProfileUrl: String?,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)