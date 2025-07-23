package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pemrogamanmobile.hydrogrow.data.remote.dto.ChatBotDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.tasks.await

class ChatBotService(private val db: FirebaseFirestore) {
    private val chatCollection = db.collection("chats")

    /**
     * Mendengarkan perubahan pada satu dokumen chat secara real-time.
     * @return Flow yang akan mengirim ChatBotDto? (nullable jika dokumen dihapus).
     */
    fun getChatByIdRealtime(chatId: String): Flow<ChatBotDto?> = callbackFlow {
        // 1. Referensi ke dokumen spesifik yang akan didengarkan
        val docRef = chatCollection.document(chatId)

        // 2. Pasang listener real-time
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error) // Tutup flow jika ada error dari Firestore
                return@addSnapshotListener
            }

            // 3. Kirim data jika dokumen ada, atau kirim null jika tidak ada (misal: sudah dihapus)
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(ChatBotDto::class.java))
            } else {
                trySend(null) // Kirim null untuk menandakan dokumen tidak ada
            }
        }

        // 4. Hapus listener saat flow dibatalkan (sangat penting untuk mencegah memory leak)
        awaitClose { listener.remove() }

    }.cancellable() // 5. Membuat flow ini aman untuk dibatalkan

    suspend fun getChatsByUserId(userId: String): List<ChatBotDto> {
        return chatCollection
            .whereEqualTo("userOwnerId", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(ChatBotDto::class.java)
    }

    suspend fun saveChat(chat: ChatBotDto) {
        chatCollection.document(chat.id).set(chat).await()
    }

    suspend fun deleteChat(chatId: String) {
        chatCollection.document(chatId).delete().await()
    }
}