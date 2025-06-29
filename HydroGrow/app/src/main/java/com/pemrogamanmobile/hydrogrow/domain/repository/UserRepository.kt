package com.pemrogamanmobile.hydrogrow.domain.repository

import com.pemrogamanmobile.hydrogrow.domain.model.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(email: String, pass: String): FirebaseUser?
    suspend fun register(email: String, pass: String): FirebaseUser?
    fun getCurrentUserId(): String?

    fun getProfile(): Flow<User?>
    suspend fun fetchAndCacheProfile()
    suspend fun updateProfile(user: User)
    suspend fun logout()
}