package com.pemrogamanmobile.hydrogrow.data.local.mapper

import com.pemrogamanmobile.hydrogrow.domain.model.User
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.UserEntity

fun UserEntity.toDomain(): User = User(
    id = id,
    email = email,
    password = password,
    name = name,
    phonenumber = phonenumber,
    address = address,
    photourl = photourl
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    password = password,
    name = name,
    phonenumber = phonenumber,
    address = address,
    photourl = photourl
)

fun List<UserEntity>.toDomainList(): List<User> = map { it.toDomain() }
fun List<User>.toEntityList(): List<UserEntity> = map { it.toEntity() }