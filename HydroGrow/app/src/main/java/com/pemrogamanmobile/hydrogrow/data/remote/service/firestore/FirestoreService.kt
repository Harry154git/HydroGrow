package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.remote.dto.UserDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val USERS_COLLECTION = "users"
    }

    // Menyimpan atau memperbarui data user di Firestore
    suspend fun saveUser(userDto: UserDto) {
        firestore.collection(USERS_COLLECTION).document(userDto.uid).set(userDto).await()
    }

    // Mengambil data user dari Firestore
    suspend fun getUser(uid: String): UserDto? {
        return firestore.collection(USERS_COLLECTION).document(uid).get().await()
            .toObject(UserDto::class.java)
    }
}