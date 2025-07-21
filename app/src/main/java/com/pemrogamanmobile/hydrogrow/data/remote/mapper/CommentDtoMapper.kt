package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.remote.dto.CommentDto
import com.pemrogamanmobile.hydrogrow.domain.model.Comment

/**
 * Mengubah CommentDto (dari Firestore) menjadi Comment (domain model).
 */
fun CommentDto.toDomain(): Comment = Comment(
    id = this.id,
    postId = this.postId,
    userCommentId = this.userCommentId,
    userName = this.userName,
    userProfileUrl = this.userProfileUrl,
    text = this.text,
    createdAt = this.createdAt
)

/**
 * Mengubah Comment (domain model) menjadi CommentDto (untuk Firestore).
 */
fun Comment.toDto(): CommentDto = CommentDto(
    id = this.id,
    postId = this.postId,
    userCommentId = this.userCommentId,
    userName = this.userName,
    userProfileUrl = this.userProfileUrl,
    text = this.text,
    createdAt = this.createdAt
)

/**
 * Mengubah list dari CommentDto menjadi list dari Comment.
 */
fun List<CommentDto>.toDomainList(): List<Comment> = map { it.toDomain() }

/**
 * Mengubah list dari Comment menjadi list dari CommentDto.
 */
fun List<Comment>.toDtoList(): List<CommentDto> = map { it.toDto() }