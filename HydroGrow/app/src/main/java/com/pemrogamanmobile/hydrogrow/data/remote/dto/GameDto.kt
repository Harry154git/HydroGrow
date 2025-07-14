package com.pemrogamanmobile.hydrogrow.data.remote.dto

import com.google.firebase.firestore.PropertyName

/**
 * Data Transfer Object (DTO) for Game stored in Firestore.
 * All properties must have default values for Firestore's automatic deserialization.
 */
data class GameDto(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("user_owner_id")
    @set:PropertyName("user_owner_id")
    var userOwnerId: String = "",

    @get:PropertyName("cup")
    @set:PropertyName("cup")
    var cup: Int = 0
)