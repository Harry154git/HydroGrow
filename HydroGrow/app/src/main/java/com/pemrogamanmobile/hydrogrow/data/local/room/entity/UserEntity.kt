package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val password: String,
    val username: String,
    val nickname: String,
    val photourl: String?
)
