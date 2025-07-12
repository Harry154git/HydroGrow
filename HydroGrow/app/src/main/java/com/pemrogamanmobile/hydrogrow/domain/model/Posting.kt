package com.pemrogamanmobile.hydrogrow.domain.model

data class Posting (
    val id: String,
    val userOwnerId: String,
    val comment: String,
    val madetime: String,
    val imageurl: String,
    val likes: Int
)