package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose // <-- Tambahkan import ini
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow // <-- Tambahkan import ini
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthService @Inject constructor(
    private val auth: FirebaseAuth
) {
    // Fungsi yang ada tetap dipertahankan
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // BARU: Fungsi untuk mendapatkan status login sebagai Flow menggunakan callbackFlow
    fun getAuthStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            // Kirim user saat ini (bisa null) ke dalam flow setiap kali ada perubahan
            trySend(firebaseAuth.currentUser)
        }
        // Mulai mendengarkan perubahan
        auth.addAuthStateListener(listener)

        // Saat flow berhenti, hentikan listener untuk mencegah memory leak
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

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