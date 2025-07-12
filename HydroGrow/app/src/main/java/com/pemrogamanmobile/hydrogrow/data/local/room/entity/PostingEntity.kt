package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posting")
data class PostingEntity (
    @PrimaryKey val id: String,
    val userOwnerId: String,
    val comment: String,
    val madetime: String,
    val imageurl: String,
    val likes: Int
)