package com.pemrogamanmobile.hydrogrow.data.remote.dto

data class UserDto(
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val nickname: String = "",
    val photourl: String? = ""
)