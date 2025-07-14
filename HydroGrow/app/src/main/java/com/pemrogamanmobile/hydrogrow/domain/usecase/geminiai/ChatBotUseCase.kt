package com.pemrogamanmobile.hydrogrow.domain.usecase.geminiai

import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.repository.ChatBotRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.GardenRepository // Asumsi ada
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val chatRepository: ChatBotRepository,
    private val gardenRepository: GardenRepository // Untuk ambil detail kebun
) {
    suspend fun sendMessage(session: ChatBot, text: String): Result<ChatBot> {
        // 1. Tambahkan pesan user dengan prefix
        session.conversation.add("user: $text")
        session.updatedAt = System.currentTimeMillis()

        // 2. Bangun prompt (dengan atau tanpa konteks)
        val prompt = buildChatPrompt(session)

        // 3. Dapatkan balasan AI
        val aiResponse = chatRepository.getAiAnalysis(prompt).getOrElse { return Result.failure(it) }
        session.conversation.add("model: $aiResponse")

        // 4. Simpan sesi terupdate
        return chatRepository.saveChatSession(session).map { session }
    }

    private suspend fun buildChatPrompt(session: ChatBot): String {
        val history = session.conversation.joinToString("\n")

        // Jika chat ini terhubung ke kebun, ambil detail kebun dan tambahkan sebagai konteks
        if (session.relatedGardenId != null) {
            val garden = gardenRepository.getGarden(session.relatedGardenId).getOrNull()
            if (garden != null) {
                val contextPrompt = "SYSTEM: Jawab pertanyaan berikut dalam konteks kebun bernama '${garden.name}'.\n---\n"
                return contextPrompt + history
            }
        }
        return history
    }
}