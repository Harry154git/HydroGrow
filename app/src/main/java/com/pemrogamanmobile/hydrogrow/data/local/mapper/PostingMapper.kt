package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.relation.PostingWithComments
import com.pemrogamanmobile.hydrogrow.domain.model.Posting

// BENAR: Mengubah PostingEntity menjadi Posting (dengan daftar komentar kosong)
fun PostingEntity.toDomain(): Posting = Posting(
    id = this.id,
    userOwnerId = this.userOwnerId,
    userOwnerName = this.userOwnerName,
    userOwnerProfileUrl = this.userOwnerProfileUrl,
    imageUrl = this.imageUrl,
    likes = this.likes,
    comments = emptyList(), // Penting!
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

// BENAR: Mengubah Posting menjadi PostingEntity (tanpa menyertakan komentar)
fun Posting.toEntity(): PostingEntity = PostingEntity(
    id = this.id,
    userOwnerId = this.userOwnerId,
    userOwnerName = this.userOwnerName,
    userOwnerProfileUrl = this.userOwnerProfileUrl,
    imageUrl = this.imageUrl,
    likes = this.likes,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

// BENAR: Fungsi ini adalah cara utama untuk mendapatkan Posting lengkap dari Room
fun PostingWithComments.toDomain(): Posting {
    return Posting(
        id = this.posting.id,
        userOwnerId = this.posting.userOwnerId,
        userOwnerName = this.posting.userOwnerName,
        userOwnerProfileUrl = this.posting.userOwnerProfileUrl,
        imageUrl = this.posting.imageUrl,
        likes = this.posting.likes,
        createdAt = this.posting.createdAt,
        updatedAt = this.posting.updatedAt,
        comments = this.comments.toDomain() // Asumsi: ada mapper List<CommentEntity>.toDomain()
    )
}

// Fungsi list ini akan otomatis benar setelah fungsi tunggalnya diperbaiki
fun List<PostingEntity>.toDomain(): List<Posting> = map { it.toDomain() }
fun List<Posting>.toEntity(): List<PostingEntity> = map { it.toEntity() }