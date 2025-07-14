package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.CommentEntity
import com.pemrogamanmobile.hydrogrow.domain.model.Comment

fun CommentEntity.toDomain(): Comment = Comment(
    id = id,
    postId = postId,
    userCommentId = userCommentId,
    userName = userName,
    userProfileUrl = userProfileUrl,
    text = text,
    createdAt = createdAt
)

fun Comment.toEntity(): CommentEntity = CommentEntity(
    id = id,
    postId = postId,
    userCommentId = userCommentId,
    userName = userName,
    userProfileUrl = userProfileUrl,
    text = text,
    createdAt = createdAt
)

fun List<CommentEntity>.toDomain(): List<Comment> = map { it.toDomain() }
fun List<Comment>.toEntity(): List<CommentEntity> = map { it.toEntity() }