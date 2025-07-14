package com.pemrogamanmobile.hydrogrow.data.repository

import com.google.firebase.auth.FirebaseUser
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.UserDao
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.UserEntity
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.AuthService
import com.pemrogamanmobile.hydrogrow.domain.model.User
import com.pemrogamanmobile.hydrogrow.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

class UserRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val firestoreService: FirestoreService,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun login(email: String, pass: String): FirebaseUser? {
        return authService.login(email, pass)
    }

    override suspend fun register(email: String, pass: String): FirebaseUser? {
        val firebaseUser = authService.register(email, pass)
        val uid = firebaseUser?.uid ?: return null

        val user = User(
            id = uid,
            email = email,
            password = pass,
            name = "Belum diisi",
            phonenumber = "Belum diisi",
            address = "Belum diisi",
            photourl = null
        )

        val map = mapOf(
            "email" to user.email,
            "password" to user.password,
            "name" to user.name,
            "phonenumber" to user.phonenumber,
            "address" to user.address,
            "photourl" to ""
        )

        firestoreService.updateProfile(uid, map)

        userDao.insertUser(user.toEntity())

        return firebaseUser
    }

    override fun getCurrentUserId(): String? = authService.getCurrentUser()?.uid

    /**
     * Return Flow dari User.
     * Strategi:
     * 1. Utamakan data lokal (Room)
     * 2. Kalau kosong, fetch dari Firestore, cache ke Room
     */
    override fun getProfile(): Flow<User?> = flow {
        val uid = getCurrentUserId()
        if (uid == null) {
            emit(null)
            return@flow
        }

        // Coba ambil dari lokal dulu
        val localEntity = userDao.observeUserById(uid).firstOrNull()

        if (localEntity != null) {
            emit(localEntity.toDomain())
        } else {
            // Kalau kosong, fetch dari Firestore
            val remoteData = firestoreService.getProfile(uid) as? Map<String, Any>
            if (remoteData != null) {
                val entity = UserEntity(
                    id = uid,
                    email = remoteData["email"] as String,
                    password = remoteData["password"] as String,
                    name = remoteData["name"] as String,
                    phonenumber = remoteData["phonenumber"] as? String ?: "Belum diisi",
                    address = remoteData["address"] as? String ?: "Belum diisi",
                    photourl = remoteData["photourl"] as? String
                )

                // Simpan ke lokal
                userDao.insertUser(entity)
                emit(entity.toDomain())
            } else {
                emit(null)
            }
        }
    }

    override suspend fun fetchAndCacheProfile() {
        val uid = getCurrentUserId() ?: return
        val data = firestoreService.getProfile(uid) as? Map<String, Any> ?: return

        val entity = UserEntity(
            id = uid,
            email = data["email"] as String,
            password = data["password"] as String,
            name = data["name"] as String,
            phonenumber = data["phonenumber"] as? String ?: "Belum diisi",
            address = data["address"] as? String ?: "Belum diisi",
            photourl = data["photourl"] as? String
        )

        userDao.insertUser(entity)
    }

    /**
     * Update profil ke lokal & Firestore sekaligus
     */
    override suspend fun updateProfile(user: User) {
        val uid = getCurrentUserId() ?: return

        val map = mapOf(
            "email" to user.email,
            "password" to user.password,
            "name" to user.name,
            "phonenumber" to user.phonenumber,
            "address" to user.address,
            "photourl" to (user.photourl ?: "")
        )

        firestoreService.updateProfile(uid, map)

        userDao.insertUser(user.toEntity())
    }

    override suspend fun logout() {
        authService.logout()
    }
}