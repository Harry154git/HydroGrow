package com.pemrogamanmobile.hydrogrow.data.repository

import com.pemrogamanmobile.hydrogrow.data.local.room.dao.ChatBotDao
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toEntity
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDto
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.ChatBotService
import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.GeminiApiService
import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.Content
import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.GeminiRequest
import com.pemrogamanmobile.hydrogrow.data.remote.service.gemini.Part
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.repository.ChatBotRepository
import com.pemrogamanmobile.hydrogrow.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val geminiApi: GeminiApiService,
    private val dao: ChatBotDao,
    private val firestore: ChatBotService // Asumsi service untuk Firestore
) : ChatBotRepository {

    override fun getChatSession(sessionId: String): Flow<Resource<ChatBot>> = flow {
        emit(Resource.Loading())
        try {
            // Network-first: Coba ambil dari Firestore
            val remoteDto = firestore.getChatBot(sessionId).getOrThrow()
            dao.upsert(remoteDto.toEntity()) // Sinkronkan ke local
            val localData = dao.getById(sessionId)!!
            emit(Resource.Success(localData.toDomain()))
        } catch (e: IOException) {
            // Gagal network, ambil dari cache lokal
            val localData = dao.getById(sessionId)
            if (localData != null) {
                emit(Resource.Success(localData.toDomain()))
            } else {
                emit(Resource.Error("Koneksi gagal dan tidak ada data lokal."))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan: ${e.message}"))
        }
    }

    override suspend fun saveChatSession(session: ChatBot): Result<Unit> {
        return try {
            dao.upsert(session.toEntity())
            firestore.saveChatBot(session.toDto()).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
}