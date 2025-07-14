package com.pemrogamanmobile.hydrogrow.domain.repository

import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.util.Resource // Asumsi Anda punya sealed class ini
import kotlinx.coroutines.flow.Flow

interface ChatBotRepository {
    /**
     * Mengambil satu sesi chat dengan strategi network-first.
     */
    fun getChatSession(sessionId: String): Flow<Resource<ChatBot>>

    /**
     * Menyimpan atau memperbarui sesi chat ke Room dan Firestore.
     */
    suspend fun saveChatSession(session: ChatBot): Result<Unit>

    /**
     * Mengirim prompt ke Gemini API dan mengembalikan HANYA teks responsnya.
     */
    suspend fun getAiAnalysis(prompt: String): Result<String>
}