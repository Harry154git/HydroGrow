package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingEntity
import com.pemrogamanmobile.hydrogrow.domain.model.Posting

/**
 * Mengubah PostingEntity dari database menjadi domain model Posting.
 */
fun PostingEntity.toDomain(): Posting = Posting(
    id = this.id,
    userOwnerId = this.userOwnerId,
    userOwnerName = this.userOwnerName,
    userOwnerProfileUrl = this.userOwnerProfileUrl,
    imageUrl = this.imageUrl,
    likes = this.likes,
    // Langsung ambil daftar komentar dari entity
    comments = this.comments,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Mengubah domain model Posting menjadi PostingEntity untuk disimpan ke database.
 */
fun Posting.toEntity(): PostingEntity = PostingEntity(
    id = this.id,
    userOwnerId = this.userOwnerId,
    userOwnerName = this.userOwnerName,
    userOwnerProfileUrl = this.userOwnerProfileUrl,
    imageUrl = this.imageUrl,
    likes = this.likes,
    // Simpan juga daftar komentar ke dalam entity
    comments = this.comments,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Mengubah daftar PostingEntity menjadi daftar domain model Posting.
 */
fun List<PostingEntity>.toDomain(): List<Posting> = map { it.toDomain() }

/**
 * Mengubah daftar domain model Posting menjadi daftar PostingEntity.
 */
fun List<Posting>.toEntity(): List<PostingEntity> = map { it.toEntity() }