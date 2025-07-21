package com.pemrogamanmobile.hydrogrow.data.repository

import com.pemrogamanmobile.hydrogrow.data.remote.dto.ResultDto
import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.Content
import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.GeminiApiService
import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.GeminiRequest
import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.Part
import com.pemrogamanmobile.hydrogrow.data.remote.service.plantnet.PlantNetApiService
import com.pemrogamanmobile.hydrogrow.domain.model.PlantInfo
import com.pemrogamanmobile.hydrogrow.domain.repository.AiRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    // Dependensi HANYA untuk layanan API
    private val geminiApi: GeminiApiService,
    private val plantApi: PlantNetApiService
) : AiRepository {

    override suspend fun getAiAnalysis(prompt: String): Result<String> {
        return try {
            val request = GeminiRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))
            val response = geminiApi.analyzeData(request)
            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("Tidak ada konten dari AI.")
            Result.success(responseText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun identifyPlant(imageFile: File, organ: String): Result<List<PlantInfo>> {
        return try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("images", imageFile.name, requestFile)
            val organPart = organ.toRequestBody("text/plain".toMediaTypeOrNull())

            // Ganti dengan API Key Pl@ntNet Anda
            val apiKey = "2b10AXHTykL4I9W4AIE3Fdv1Ru"

            val response = plantApi.identifyPlant(
                images = imagePart,
                organs = organPart,
                apiKey = apiKey
            )

            val domainResult = response.results.map { it.toPlantInfo() }
            Result.success(domainResult)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun ResultDto.toPlantInfo(): PlantInfo {
        return PlantInfo(
            scientificName = this.species.scientificName,
            commonName = this.species.commonNames.firstOrNull() ?: "Tidak ada nama umum",
            score = this.score
        )
    }
}