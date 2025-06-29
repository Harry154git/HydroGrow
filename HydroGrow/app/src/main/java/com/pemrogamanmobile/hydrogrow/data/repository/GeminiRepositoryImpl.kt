package com.pemrogamanmobile.hydrogrow.data.repository

import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.*
import com.pemrogamanmobile.hydrogrow.domain.repository.GeminiRepository
import javax.inject.Inject

class GeminiRepositoryImpl @Inject constructor(
    private val apiService: GeminiApiService
) : GeminiRepository {

    override suspend fun analyze(input: String): String {
        val fullPrompt = """
        Anda adalah asisten ahli hidroponik yang membantu petani selada membuat kebun baru. 
        Tolong analisis data berikut dan berikan:
        1. Tipe hidroponik yang cocok.
        2. Estimasi biaya dalam rupiah.
        3. Luas kebun dalam meter persegi.
        Jelaskan juga alasan pemilihan tersebut.

        Data:
        $input
    """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = fullPrompt))
                )
            )
        )

        val response = apiService.analyzeData(request)

        return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "Tidak ada respon dari AI"
    }

    override suspend fun analyzeConversation(messages: List<String>): String {
        val prompt = messages.joinToString("\n")

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )
        val response = apiService.analyzeData(request)
        return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "Tidak ada respon dari AI"
    }
}