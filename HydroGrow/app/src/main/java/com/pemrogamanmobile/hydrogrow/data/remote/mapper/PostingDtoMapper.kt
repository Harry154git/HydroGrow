package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.remote.dto.PostingDto
import com.pemrogamanmobile.hydrogrow.domain.model.Posting

/**
 * Mengubah PostingDto menjadi Posting. Kode ini tidak berubah.
 */
fun PostingDto.toDomain(): Posting = Posting(
    id = this.id,
    userOwnerId = this.userOwnerId,
    userOwnerName = this.userOwnerName,
    userOwnerProfileUrl = this.userOwnerProfileUrl,
    imageUrl = this.imageUrl,
    likes = this.likes,
    // Baris ini sekarang akan menggunakan CommentDtoMapper yang sudah disederhanakan.
    comments = this.comments.toDomainList(),
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Mengubah Posting menjadi PostingDto. Kode ini tidak berubah.
 */
fun Posting.toDto(): PostingDto = PostingDto(
    id = this.id,
    userOwnerId = this.userOwnerId,
    userOwnerName = this.userOwnerName,
    userOwnerProfileUrl = this.userOwnerProfileUrl,
    imageUrl = this.imageUrl,
    likes = this.likes,
    // Baris ini sekarang akan menggunakan CommentDtoMapper yang sudah disederhanakan.
    comments = this.comments.toDtoList(),
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Mengubah list dari PostingDto menjadi list dari Posting.
 */
fun List<PostingDto>.toDomainList(): List<Posting> = map { it.toDomain() }

/**
 * Mengubah list dari Posting menjadi list dari PostingDto.
 */
fun List<Posting>.toDtoList(): List<PostingDto> = map { it.toDto() }