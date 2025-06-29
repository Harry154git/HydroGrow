package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val password: String,
    val name: String,
    val phonenumber: String = "Belum diisi",
    val address: String = "Belum diisi",
    val photourl: String?
)
