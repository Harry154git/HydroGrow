package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices

import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.remote.dto.ChatBotDto
import kotlinx.coroutines.tasks.await

class ChatBotService(private val db: FirebaseFirestore) {

    private fun chatBotCol(userId: String) = db.collection("users").document(userId).collection("chatbot")

    // Mengambil histori percakapan
    suspend fun getChatHistory(userId: String, chatId: String): ChatBotDto? {
        return chatBotCol(userId).document(chatId).get().await().toObject(ChatBotDto::class.java)
    }

    // Menyimpan atau mengupdate percakapan
    suspend fun saveChatHistory(userId: String, chat: ChatBotDto) {
        chatBotCol(userId).document(chat.id).set(chat).await()
    }
}