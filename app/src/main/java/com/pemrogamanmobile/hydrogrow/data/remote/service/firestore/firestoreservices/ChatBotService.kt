package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pemrogamanmobile.hydrogrow.data.remote.dto.ChatBotDto
import kotlinx.coroutines.tasks.await

class ChatBotService(private val db: FirebaseFirestore) {
    private val chatCollection = db.collection("chats")

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