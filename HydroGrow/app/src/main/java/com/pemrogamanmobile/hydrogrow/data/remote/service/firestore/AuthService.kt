package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthService @Inject constructor(
    private val auth: FirebaseAuth
) {
    // Mengembalikan user yang sedang login dari FirebaseAuth
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // Fungsi sign-in umum yang menerima credential
    suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser? {
        return try {
            auth.signInWithCredential(credential).await().user
        } catch (e: Exception) {
            // Log error atau tangani di sini jika perlu
            throw e // Lemparkan exception agar bisa ditangani oleh repository
        }
    }

    // Fungsi untuk logout
    suspend fun signOut() {
        auth.signOut()
    }
}