package com.pemrogamanmobile.hydrogrow.data.remote.service.gemini

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)