package com.pemrogamanmobile.hydrogrow.domain.model

data class User(
    val id: String,
    val email: String,
    val password: String,
    val name: String,
    val phonenumber: String = "Belum diisi",
    val address: String = "Belum diisi",
    val photourl: String?
)