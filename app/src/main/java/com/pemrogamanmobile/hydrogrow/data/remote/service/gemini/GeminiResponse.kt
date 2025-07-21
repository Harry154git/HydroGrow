package com.pemrogamanmobile.hydrogrow.data.remote.service.gemini

data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: Content?
)