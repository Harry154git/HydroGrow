package com.pemrogamanmobile.hydrogrow.domain.repository

import com.pemrogamanmobile.hydrogrow.domain.model.PlantInfo
import java.io.File

interface AiRepository {
    /**
     * Mengidentifikasi tanaman dari gambar menggunakan PlantNet API.
     */
    suspend fun identifyPlant(imageFile: File, organ: String = "auto"): Result<List<PlantInfo>>

    /**
     * Mengirim prompt ke Gemini AI untuk analisis.
     */
    suspend fun getAiAnalysis(prompt: String): Result<String>
}