package com.pemrogamanmobile.hydrogrow.domain.repository

interface GeminiRepository {
    suspend fun analyze(input: String): String
    suspend fun analyzeConversation(messages: List<String>): String
}