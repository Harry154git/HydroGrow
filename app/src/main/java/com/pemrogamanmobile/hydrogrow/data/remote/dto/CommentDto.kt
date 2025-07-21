package com.pemrogamanmobile.hydrogrow.data.remote.dto

import com.google.firebase.firestore.PropertyName

/**
 * DTO untuk Comment (versi sederhana tanpa balasan).
 */
data class CommentDto(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("post_id")
    @set:PropertyName("post_id")
    var postId: String = "",

    @get:PropertyName("user_comment_id")
    @set:PropertyName("user_comment_id")
    var userCommentId: String = "",

    @get:PropertyName("user_name")
    @set:PropertyName("user_name")
    var userName: String = "",

    @get:PropertyName("user_profile_url")
    @set:PropertyName("user_profile_url")
    var userProfileUrl: String? = null,

    @get:PropertyName("text")
    @set:PropertyName("text")
    var text: String = "",

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Long = 0L
)