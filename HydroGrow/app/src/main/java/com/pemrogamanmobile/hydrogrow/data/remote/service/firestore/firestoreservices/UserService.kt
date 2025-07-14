package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserService(private val db: FirebaseFirestore) {

    // Helper untuk mendapatkan referensi ke koleksi 'users'
    private val usersCol = db.collection("users")

    /**
     * Mengambil data profil user sebagai Map.
     * Mengembalikan null jika dokumen user tidak ada.
     */
    suspend fun getProfile(uid: String): Map<String, Any>? {
        return usersCol.document(uid).get().await().data
    }

    /**
     * Membuat atau mengupdate data profil user.
     * Metode `set` akan menimpa seluruh data pada dokumen dengan data yang baru.
     * Jika dokumen belum ada, dokumen baru akan dibuat.
     */
    suspend fun updateProfile(uid: String, data: Map<String, Any>) {
        usersCol.document(uid).set(data).await()
    }
}