package com.pemrogamanmobile.hydrogrow.presentation.mapper

import com.pemrogamanmobile.hydrogrow.domain.model.User
import com.pemrogamanmobile.hydrogrow.presentation.model.UserUi

fun User.toUi(): UserUi = UserUi(
    name = name,
    email = email,
    phone = phonenumber,
    address = address,
    photoUrl = photourl,
    password = password
)

fun UserUi.toDomain(id: String): User = User(
    id = id,
    email = email,
    password = password,
    name = name,
    phonenumber = phone,
    address = address,
    photourl = photoUrl
)
