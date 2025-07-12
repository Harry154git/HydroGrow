package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingEntity
import com.pemrogamanmobile.hydrogrow.domain.model.Posting

/**
 * Converts a PostingEntity (from the database) to a Posting (domain model).
 */
fun PostingEntity.toDomain(): Posting = Posting(
    id = this.id,
    userOwnerId = this.userOwnerId,
    comment = this.comment,
    imageUrl = this.imageUrl,
    likes = this.likes,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

/**
 * Converts a Posting (domain model) to a PostingEntity (for the database).
 */
fun Posting.toEntity(): PostingEntity = PostingEntity(
    id = this.id,
    userOwnerId = this.userOwnerId,
    comment = this.comment,
    imageUrl = this.imageUrl,
    likes = this.likes,
    createdAt = this.createdAt, // Preserves the original creation timestamp
    updatedAt = System.currentTimeMillis() // Sets the update timestamp to now
)

/**
 * Converts a list of PostingEntity objects to a list of Posting domain models.
 */
fun List<PostingEntity>.toDomainList(): List<Posting> = map { it.toDomain() }

/**
 * Converts a list of Posting domain models to a list of PostingEntity objects.
 */
fun List<Posting>.toEntityList(): List<PostingEntity> = map { it.toEntity() }