package com.pemrogamanmobile.hydrogrow.domain.model

data class User(
    val id: String,
    val email: String,
    val password: String,
    val username: String,
    val nickname: String,
    val photourl: String?
)