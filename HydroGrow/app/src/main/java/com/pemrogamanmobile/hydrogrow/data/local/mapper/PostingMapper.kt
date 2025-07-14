package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.relation.PostingWithComments
import com.pemrogamanmobile.hydrogrow.domain.model.Posting

/**
 * Mengubah objek relasi dari database menjadi objek domain yang bersih.
 */
fun PostingWithComments.toDomain(): Posting = Posting(
    id = posting.id,
    userOwnerId = posting.userOwnerId,
    userOwnerName = posting.userOwnerName,
    userOwnerProfileUrl = posting.userOwnerProfileUrl,
    imageUrl = posting.imageUrl,
    likes = posting.likes,
    comments = comments.toDomain(), // Memanggil mapper komentar di sini
    createdAt = posting.createdAt,
    updatedAt = posting.updatedAt
)

/**
 * Mengubah daftar objek relasi menjadi daftar objek domain.
 */
fun List<PostingWithComments>.toDomain(): List<Posting> = map { it.toDomain() }

/**
 * Mengubah objek domain menjadi entitas postingan (tanpa komentar).
 * Berguna saat menyimpan postingan ke database. Komentar disimpan terpisah.
 */
fun Posting.toEntity(): PostingEntity = PostingEntity(
    id = id,
    userOwnerId = userOwnerId,
    userOwnerName = userOwnerName,
    userOwnerProfileUrl = userOwnerProfileUrl,
    imageUrl = imageUrl,
    likes = likes,
    createdAt = createdAt,
    updatedAt = updatedAt
)