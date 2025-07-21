package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingWithComments
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
 * Mengubah objek relasi PostingWithComments (dari database lokal)
 * menjadi objek domain Posting yang bersih.
 */
fun PostingWithComments.toDomain(): Posting {
    return Posting(
        // Ambil properti utama dari `posting` (PostingEntity)
        id = this.posting.id,
        userOwnerId = this.posting.userOwnerId,
        userOwnerName = this.posting.userOwnerName,
        userOwnerProfileUrl = this.posting.userOwnerProfileUrl,
        imageUrl = this.posting.imageUrl,
        likes = this.posting.likes,
        createdAt = this.posting.createdAt,
        updatedAt = this.posting.updatedAt,

        // Ambil daftar komentar, lalu konversi juga ke domain model
        // Ini akan memanggil mapper `List<CommentEntity>.toDomain()` yang sudah ada
        comments = this.comments.toDomain()
    )
}

/**
 * Mengubah daftar PostingEntity menjadi daftar domain model Posting.
 */
fun List<PostingEntity>.toDomain(): List<Posting> = map { it.toDomain() }

/**
 * Mengubah daftar domain model Posting menjadi daftar PostingEntity.
 */
fun List<Posting>.toEntity(): List<PostingEntity> = map { it.toEntity() }