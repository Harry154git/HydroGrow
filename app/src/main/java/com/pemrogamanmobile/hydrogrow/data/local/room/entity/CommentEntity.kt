package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    foreignKeys = [ForeignKey(
        entity = PostingEntity::class,
        parentColumns = ["id"],
        childColumns = ["postId"],
        onDelete = ForeignKey.CASCADE // Jika postingan dihapus, komentar ikut terhapus
    )]
)
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String, // Foreign key yang merujuk ke PostingEntity
    val userCommentId: String,
    val userName: String,
    val userProfileUrl: String?,
    val text: String,
    val createdAt: Long
)