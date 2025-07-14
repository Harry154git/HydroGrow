package com.pemrogamanmobile.hydrogrow.data.remote.mapper

import com.pemrogamanmobile.hydrogrow.data.remote.dto.UserDto
import com.pemrogamanmobile.hydrogrow.domain.model.User

fun UserDto.toDomain(): User = User(
    id = id,
    email = email,
    password = password,
    username = username,
    nickname = nickname,
    photourl = photourl
)

fun User.toDto(): UserDto = UserDto(
    id = id,
    email = email,
    password = password,
    username = username,
    nickname = nickname,
    photourl = photourl
)

fun List<UserDto>.toDomainList(): List<User> = map { it.toDomain() }
fun List<User>.toDtoList(): List<UserDto> = map { it.toDto() }