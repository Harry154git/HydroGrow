package com.pemrogamanmobile.hydrogrow.domain.repository

import com.google.firebase.auth.AuthCredential
import com.pemrogamanmobile.hydrogrow.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // Fungsi untuk mendapatkan user yang sedang login
    fun getSignedInUser(): User?

    val cachedUser: Flow<User?>

    // Fungsi untuk logout
    suspend fun signOut()

    // Fungsi untuk sign-in dengan kredensial (termasuk dari Google)
    suspend fun signInWithCredential(credential: AuthCredential): User?
}