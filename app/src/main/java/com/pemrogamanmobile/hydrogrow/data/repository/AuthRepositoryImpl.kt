package com.pemrogamanmobile.hydrogrow.data.repository

import com.google.firebase.auth.AuthCredential
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDomain // Pastikan mapper di-import
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDto
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.AuthService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.FirestoreService
import com.pemrogamanmobile.hydrogrow.domain.model.User
import com.pemrogamanmobile.hydrogrow.domain.repository.AuthRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val firestoreService: FirestoreService, // Inject FirestoreService
    private val preferencesRepository: PreferencesRepository // Inject PreferencesRepository
) : AuthRepository {

    // Mengambil user yang sedang login, langsung dari Firebase Auth
    override fun getSignedInUser(): User? {
        // Memakai fungsi mapper terpusat
        return authService.getCurrentUser()?.toDomain()
    }

    // BARU: Implementasi untuk mendapatkan status login sebagai Flow
    override fun getAuthStateFlow(): Flow<User?> {
        return authService.getAuthStateFlow().map { firebaseUser ->
            // Ubah FirebaseUser? menjadi User? (domain model)
            firebaseUser?.toDomain()
        }
    }

    // Alirkan data user dari cache (DataStore) untuk UI
    override val cachedUser: Flow<User?>
        get() = preferencesRepository.cachedUser

    override suspend fun signOut() {
        authService.signOut()
        preferencesRepository.clearUserCache() // Hapus cache saat logout
    }

    override suspend fun signInWithCredential(credential: AuthCredential): User? {
        val firebaseUser = authService.signInWithCredential(credential)

        return firebaseUser?.toDomain()?.also { user -> // Memakai fungsi mapper terpusat
            firestoreService.saveUser(user.toDto())
            preferencesRepository.saveUserToCache(user)
        }
    }
}