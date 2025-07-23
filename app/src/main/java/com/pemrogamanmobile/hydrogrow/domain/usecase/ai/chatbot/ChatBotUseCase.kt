package com.pemrogamanmobile.hydrogrow.domain.usecase.ai.chatbot

import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage
import com.pemrogamanmobile.hydrogrow.domain.repository.AiRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.AuthRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.ChatBotRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.GardenRepository
import com.pemrogamanmobile.hydrogrow.domain.repository.PlantRepository
import com.pemrogamanmobile.hydrogrow.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.UUID
import javax.inject.Inject

class ChatBotUseCase @Inject constructor(
    private val chatRepository: ChatBotRepository,
    private val aiRepository: AiRepository,
    private val gardenRepository: GardenRepository,
    private val plantRepository: PlantRepository,
    private val authRepository: AuthRepository
) {
    /**
     * ✅ FIX: Mengambil riwayat percakapan dari sumber data lokal (Single Source of Truth).
     * @return Flow yang akan meng-emit daftar percakapan setiap kali ada perubahan.
     */
    fun getChatHistory(): Flow<List<ChatBot>> {
        return chatRepository.getChatHistory()
    }

    /**
     * ✅ BARU: Memicu pembaruan data riwayat percakapan dari jaringan.
     */
    suspend fun refreshChatHistory(): Resource<Unit> {
        return chatRepository.refreshChatHistory()
    }

    /**
     * Mengambil data percakapan spesifik berdasarkan ID-nya secara real-time.
     */
    fun getChatBotById(chatbotid: String): Flow<Resource<ChatBot>> {
        return chatRepository.getChatById(chatbotid)
    }

    /**
     * Melanjutkan percakapan yang sudah ada. Logika ini sudah baik dan tidak perlu diubah.
     */
    fun continueConversation(chatbotid: String, text: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        val chatResource = chatRepository.getChatById(chatbotid).first { it !is Resource.Loading }
        val chat = (chatResource as? Resource.Success)?.data
            ?: throw IllegalStateException(chatResource.message ?: "Gagal memuat data percakapan.")

        chat.conversation.add(ChatMessage(role = "user", content = text, timestamp = System.currentTimeMillis()))
        val historyPrompt = chat.conversation.joinToString("\n") { "${it.role}: ${it.content}" }
        val responseText = aiRepository.getAiAnalysis(historyPrompt).getOrThrow()

        val aiMessage = ChatMessage(role = "model", content = responseText, timestamp = System.currentTimeMillis())
        chat.conversation.add(aiMessage)
        val updateResult = chatRepository.updateChat(chat)
        emit(updateResult)

    }.catch { e ->
        emit(Resource.Error("Terjadi kesalahan saat melanjutkan percakapan: ${e.message}"))
    }

    /**
     * ✅ FIX: Memulai percakapan baru dengan logika yang lebih rata (flat).
     */
    fun startNewConversation(text: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val currentUser = authRepository.getSignedInUser() ?: throw SecurityException("Pengguna tidak terautentikasi.")

        val responseText = aiRepository.getAiAnalysis(text).getOrThrow()
        val titlePrompt = "Buat judul singkat (maksimal 5 kata) untuk percakapan ini:\nUser: $text\nAI: $responseText"
        val title = aiRepository.getAiAnalysis(titlePrompt).getOrThrow()

        val userMessage = ChatMessage(role = "user", content = text, timestamp = System.currentTimeMillis())
        val aiMessage = ChatMessage(role = "model", content = responseText, timestamp = System.currentTimeMillis())

        val newChat = ChatBot(
            id = UUID.randomUUID().toString(),
            userOwnerId = currentUser.uid,
            title = title.trim().removeSurrounding("\""),
            conversation = mutableListOf(userMessage, aiMessage),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val addResult = chatRepository.addChat(newChat)
        emit(addResult)
    }.catch { e ->
        emit(Resource.Error("Gagal memulai percakapan baru: ${e.message}"))
    }

    /**
     * ✅ FIX: Memulai percakapan baru dengan deteksi gambar dengan logika yang lebih rata.
     */
    fun startNewConversationWithImageDetection(text: String, imageFile: File): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val currentUser = authRepository.getSignedInUser() ?: throw SecurityException("Pengguna tidak terautentikasi.")

        val plantInfoList = aiRepository.identifyPlant(imageFile, "auto").getOrThrow()
        if (plantInfoList.isEmpty()) {
            throw IllegalStateException("Tanaman tidak dapat diidentifikasi.")
        }

        val topPlant = plantInfoList.first()
        val combinedPrompt = """...""" // Prompt Anda di sini
        val responseText = aiRepository.getAiAnalysis(combinedPrompt).getOrThrow()

        val finalResponse = "Ini adalah tanaman: **${topPlant.commonName}** (*${topPlant.scientificName}*).\n\n$responseText"
        val userMessage = ChatMessage(role = "user", content = text, timestamp = System.currentTimeMillis())
        val aiMessage = ChatMessage(role = "model", content = finalResponse, timestamp = System.currentTimeMillis())

        val titlePrompt = "Buat judul singkat (maksimal 5 kata) untuk percakapan tentang tanaman ${topPlant.commonName} ini."
        val title = aiRepository.getAiAnalysis(titlePrompt).getOrThrow()

        val newChat = ChatBot(
            id = UUID.randomUUID().toString(),
            userOwnerId = currentUser.uid,
            title = title.trim().removeSurrounding("\""),
            conversation = mutableListOf(userMessage, aiMessage),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val addResult = chatRepository.addChat(newChat)
        emit(addResult)
    }.catch { e ->
        emit(Resource.Error("Terjadi kesalahan pada deteksi gambar: ${e.message}"))
    }


    /**
     * ✅ FIX: Memulai percakapan baru berdasarkan data pilihan dengan logika yang lebih rata.
     */
    fun startNewConversationWithChosenData(text: String, id: String, contextType: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val currentUser = authRepository.getSignedInUser() ?: throw SecurityException("Pengguna tidak terautentikasi.")

        val prompt = when (contextType.lowercase()) {
            "garden" -> buildGardenPrompt(id, text)
            "plant" -> buildPlantPrompt(id, text)
            else -> Result.failure(IllegalArgumentException("Tipe konteks tidak valid: $contextType"))
        }.getOrThrow()

        val responseText = aiRepository.getAiAnalysis(prompt).getOrThrow()
        val titlePrompt = "Buat judul singkat (maksimal 5 kata) untuk percakapan ini:\nUser: $text\nAI: $responseText"
        val title = aiRepository.getAiAnalysis(titlePrompt).getOrThrow()

        val userMessage = ChatMessage(role = "user", content = text, timestamp = System.currentTimeMillis())
        val aiMessage = ChatMessage(role = "model", content = responseText, timestamp = System.currentTimeMillis())

        val newChat = ChatBot(
            id = UUID.randomUUID().toString(),
            userOwnerId = currentUser.uid,
            title = title.trim().removeSurrounding("\""),
            conversation = mutableListOf(userMessage, aiMessage),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val addResult = chatRepository.addChat(newChat)
        emit(addResult)
    }.catch { e ->
        emit(Resource.Error("Gagal memulai percakapan dengan data pilihan: ${e.message}"))
    }


    // Helper functions tidak perlu diubah.
    private suspend fun buildGardenPrompt(id: String, userQuestion: String): Result<String> {
        val garden = gardenRepository.getGardenById(id)
        return if (garden != null) {
            val prompt = """
            Konteks: Pengguna bertanya tentang taman hidroponik mereka yang bernama "${garden.gardenName}".
            Detail Taman: Tipe hidroponik ${garden.hydroponicType} dengan ukuran ${garden.gardenSize} meter persegi.
            
            Pertanyaan Pengguna: "$userQuestion"
            
            Jawab pertanyaan tersebut berdasarkan konteks taman yang diberikan.
        """.trimIndent()
            Result.success(prompt)
        } else {
            Result.failure(Exception("Data taman dengan ID $id tidak ditemukan."))
        }
    }

    private suspend fun buildPlantPrompt(id: String, userQuestion: String): Result<String> {
        val plant = plantRepository.getPlantById(id).firstOrNull()
        return if (plant != null) {
            val prompt = """
            Konteks: Pengguna bertanya tentang tanaman mereka yaitu "${plant.plantName}".
            Informasi Tanaman: Perkiraan waktu panen untuk tanaman ini adalah ${plant.harvestTime}.
            
            Pertanyaan Pengguna: "$userQuestion"
            
            Jawab pertanyaan tersebut berdasarkan konteks tanaman yang diberikan.
        """.trimIndent()
            Result.success(prompt)
        } else {
            Result.failure(Exception("Data tanaman dengan ID $id tidak ditemukan."))
        }
    }
}