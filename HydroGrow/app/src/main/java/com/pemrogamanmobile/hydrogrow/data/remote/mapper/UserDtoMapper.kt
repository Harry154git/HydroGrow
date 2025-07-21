package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.google.firebase.auth.FirebaseUser // Tambahkan import ini
import com.pemrogamanmobile.hydrogrow.data.remote.dto.UserDto
import com.pemrogamanmobile.hydrogrow.domain.model.User

// Dari DTO -> Domain
fun UserDto.toDomain(): User = User(
    uid = uid,
    name = name,
    email = email,
    photoUrl = photoUrl
)

// Dari FirebaseUser -> Domain (FUNGSI BARU)
fun FirebaseUser.toDomain(): User = User(
    uid = this.uid,
    name = this.displayName,
    email = this.email,
    photoUrl = this.photoUrl?.toString()
)

// Dari Domain -> DTO
fun User.toDto(): UserDto = UserDto(
    uid = uid,
    name = name,
    email = email,
    photoUrl = photoUrl
)