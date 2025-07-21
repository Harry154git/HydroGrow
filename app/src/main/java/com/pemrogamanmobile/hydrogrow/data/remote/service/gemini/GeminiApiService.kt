package com.pemrogamanmobile.hydrogrow.data.remote.service.gemini

import retrofit2.http.Body
import retrofit2.http.POST

interface GeminiApiService {
    @POST("v1/models/gemini-1.5-pro:generateContent")
    suspend fun analyzeData(@Body request: GeminiRequest): GeminiResponse
}

