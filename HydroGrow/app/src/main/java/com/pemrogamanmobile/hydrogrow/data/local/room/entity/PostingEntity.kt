package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pemrogamanmobile.hydrogrow.domain.model.Comment

@Entity(tableName = "posting")
data class PostingEntity(
    @PrimaryKey
    val id: String,
    val userOwnerId: String,
    val userOwnerName: String,
    val userOwnerProfileUrl: String?,
    val imageUrl: String?,
    val likes: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val comments: List<Comment>
)