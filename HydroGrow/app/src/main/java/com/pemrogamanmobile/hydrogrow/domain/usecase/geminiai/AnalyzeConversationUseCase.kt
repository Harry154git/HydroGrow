package com.pemrogamanmobile.hydrogrow.domain.usecase.geminiai

import com.pemrogamanmobile.hydrogrow.domain.repository.GeminiRepository
import javax.inject.Inject

class AnalyzeConversationUseCase @Inject constructor(
    private val repository: GeminiRepository
) {
    suspend fun execute(messages: List<String>): String {
        return repository.analyzeConversation(messages = messages)
    }
}