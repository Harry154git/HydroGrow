package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posting")
data class PostingEntity(
    @PrimaryKey val id: String,

    @ColumnInfo(name = "user_owner_id")
    val userOwnerId: String,

    val comment: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String,

    val likes: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)