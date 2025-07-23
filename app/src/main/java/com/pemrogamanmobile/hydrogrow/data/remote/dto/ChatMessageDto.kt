package com.pemrogamanmobile.hydrogrow.data.remote.dto

import com.google.firebase.firestore.PropertyName

data class ChatMessageDto(
    @get:PropertyName("role") @set:PropertyName("role")
    var role: String = "",

    @get:PropertyName("content") @set:PropertyName("content")
    var content: String = "",

    @get:PropertyName("timestamp") @set:PropertyName("timestamp")
    var timestamp: Long = 0,

    // FIX: Tambahkan properti ini agar cocok dengan model domain
    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl")
    var imageUrl: String? = null
)