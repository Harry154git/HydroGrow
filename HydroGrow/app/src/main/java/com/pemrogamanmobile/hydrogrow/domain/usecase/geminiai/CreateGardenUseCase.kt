package com.pemrogamanmobile.hydrogrow.domain.usecase.geminiai

import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.repository.ChatBotRepository
import java.util.UUID
import javax.inject.Inject

class CreateGardenUseCase @Inject constructor (
    private val repository: ChatBotRepository
) {

    private val questions = listOf(
        "Berapa anggaran Anda (dalam Rupiah)?",
        "Berapa luas lahan Anda (dalam meter persegi)?",
        "Apa jenis tanaman yang ingin ditanam?"
    )

    suspend fun startWizard(userId: String): Result<ChatBot> {
        val newSession = ChatBot(
            id = UUID.randomUUID().toString(),
            userOwnerId = userId,
            title = "Analisis Kebun Baru",
            conversation = mutableListOf("model: Mari mulai analisis kebun baru! " + questions.first())
        )
        return repository.saveChatSession(newSession).map { newSession }
    }

    suspend fun processUserResponse(session: ChatBot, userAnswer: String): Result<ChatBot> {
        // 1. Tambahkan jawaban user dengan prefix
        session.conversation.add("user: $userAnswer")
        session.updatedAt = System.currentTimeMillis()

        // 2. Cek apakah wizard selesai
        val answeredCount = session.conversation.count { it.startsWith("user:") }
        if (answeredCount < questions.size) {
            // 3. Jika belum, ajukan pertanyaan berikutnya
            session.conversation.add("model: ${questions[answeredCount]}")
        } else {
            // 4. Jika selesai, lakukan analisis final
            val finalPrompt = buildFinalAnalysisPrompt(session)
            val analysisResult = repository.getAiAnalysis(finalPrompt).getOrElse { return Result.failure(it) }
            session.conversation.add("model: Terima kasih! Berikut hasil analisisnya:\n\n$analysisResult")
        }

        // 5. Simpan sesi yang sudah diperbarui
        return repository.saveChatSession(session).map { session }
    }

    private fun buildFinalAnalysisPrompt(session: ChatBot): String {
        // Ekstrak jawaban user dari list percakapan
        val answers = session.conversation
            .filter { it.startsWith("user:") }
            .map { it.removePrefix("user: ").trim() }

        val data = """
            - Anggaran: ${answers.getOrNull(0) ?: "N/A"}
            - Luas Lahan: ${answers.getOrNull(1) ?: "N/A"}
            - Jenis Tanaman: ${answers.getOrNull(2) ?: "N/A"}
        """.trimIndent()

        return "Anda adalah ahli hidroponik. Analisis data ini dan berikan rekomendasi lengkap.\nData:\n$data"
    }
}