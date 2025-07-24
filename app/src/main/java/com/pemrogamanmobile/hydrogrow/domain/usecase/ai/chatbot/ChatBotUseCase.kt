package com.pemrogamanmobile.hydrogrow.domain.usecase.ai.chatbot

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.ImageUploader
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage
import com.pemrogamanmobile.hydrogrow.domain.model.OnboardingPreferences
import com.pemrogamanmobile.hydrogrow.domain.repository.*
import com.pemrogamanmobile.hydrogrow.util.Resource
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.UUID
import javax.inject.Inject

class ChatBotUseCase @Inject constructor(
    private val chatRepository: ChatBotRepository,
    private val aiRepository: AiRepository,
    private val gardenRepository: GardenRepository,
    private val plantRepository: PlantRepository,
    private val authRepository: AuthRepository,
    private val imageUploader: ImageUploader,
    private val onboardingRepository: OnboardingRepository // [DIUBAH] Menambahkan repository onboarding
) {

    fun getChatHistory(): Flow<List<ChatBot>> {
        return chatRepository.getChatHistory()
    }

    suspend fun refreshChatHistory(): Resource<Unit> {
        return chatRepository.refreshChatHistory()
    }

    fun getChatBotById(chatbotid: String): Flow<Resource<ChatBot>> {
        return chatRepository.getChatById(chatbotid)
    }

    fun continueConversation(chatbotid: String, message: ChatMessage, imageFile: File?): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        val currentUser = authRepository.getSignedInUser()
            ?: throw SecurityException("Pengguna tidak terautentikasi untuk melanjutkan percakapan.")

        val imageUrl = if (imageFile != null) {
            imageUploader.uploadImageToStorage(imageFile.toUri(), "chatbot_images", currentUser.uid)
        } else {
            null
        }

        val finalUserMessage = message.copy(imageUrl = imageUrl)

        val addUserMessageResult = chatRepository.addMessageToChat(chatbotid, finalUserMessage, null)
        if (addUserMessageResult is Resource.Error) {
            emit(addUserMessageResult)
            return@flow
        }

        val prompt: String
        if (imageFile != null) {
            val plantInfoList = aiRepository.identifyPlant(imageFile, "auto").getOrThrow()
            val topPlant = plantInfoList.firstOrNull()

            if (topPlant != null) {
                prompt = """
                    Pengguna melanjutkan percakapan dan mengirim gambar baru.
                    Hasil identifikasi gambar baru:
                    - Nama Umum: ${topPlant.commonName}
                    - Nama Ilmiah: ${topPlant.scientificName}
                    
                    Konteks percakapan sebelumnya juga tersedia. Jawab pertanyaan pengguna terkait gambar baru ini.
                    
                    Pertanyaan Pengguna: "${message.content}"
                """.trimIndent()
            } else {
                prompt = "Pengguna mengirim gambar yang tidak dapat diidentifikasi. Jawab pertanyaan pengguna: \"${message.content}\""
            }
        } else {
            val chatResource = chatRepository.getChatById(chatbotid).first { it !is Resource.Loading }
            val chat = (chatResource as? Resource.Success)?.data
                ?: throw IllegalStateException("Gagal memuat data percakapan.")
            prompt = "Konteks riwayat percakapan sudah diberikan. Jawab pertanyaan terakhir dari pengguna: \"${message.content}\""
        }

        val responseText = aiRepository.getAiAnalysis(prompt).getOrThrow()
        val aiMessage = ChatMessage(role = "model", content = responseText, timestamp = System.currentTimeMillis())
        val addAiMessageResult = chatRepository.addMessageToChat(chatbotid, aiMessage, null)
        emit(addAiMessageResult)

    }.catch { e ->
        emit(Resource.Error("Terjadi kesalahan saat melanjutkan percakapan: ${e.message}"))
    }

    fun startNewConversation(text: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val currentUser = authRepository.getSignedInUser() ?: throw SecurityException("Pengguna tidak terautentikasi.")

        // [DIUBAH] Ambil preferensi user dan bangun prompt dengan konteks pengguna
        val preferences = onboardingRepository.getOnboardingPreferences(currentUser.uid).first()
        val userContextPrompt = if (preferences != null) {
            "Anda berbicara dengan pengguna yang memiliki pengalaman '${preferences.experience}' dan ketersediaan waktu '${preferences.timeAvailable}'. Sesuaikan jawaban Anda dengan konteks ini."
        } else ""

        val finalPrompt = """
            $userContextPrompt
            
            Jawab pertanyaan pengguna berikut: "$text"
        """.trimIndent()

        val responseText = aiRepository.getAiAnalysis(finalPrompt).getOrThrow()

        val titlePrompt = """
            Tugas: Berikan satu judul yang sangat singkat (3-5 kata) untuk sebuah percakapan. Jangan tambahkan tanda kutip atau kata pengantar.
            CONTOH:
            Input: Percakapan tentang cara merawat selada hidroponik.
            Judul: Merawat Selada Hidroponik
            ---
            Input: Percakapan tentang pertanyaan "$text".
            Judul:
        """.trimIndent()
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
        if (addResult is Resource.Success) {
            emit(Resource.Success(newChat.id))
        } else if (addResult is Resource.Error) {
            emit(Resource.Error(addResult.message ?: "Gagal menyimpan chat baru."))
        }
    }.catch { e ->
        emit(Resource.Error("Gagal memulai percakapan baru: ${e.message}"))
    }

    fun startNewConversationWithImageDetection(text: String, imageFile: File): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val currentUser = authRepository.getSignedInUser() ?: throw SecurityException("Pengguna tidak terautentikasi.")

        // [DIUBAH] Ambil preferensi user
        val preferences = onboardingRepository.getOnboardingPreferences(currentUser.uid).first()

        val plantInfoList = aiRepository.identifyPlant(imageFile, "auto").getOrThrow()
        if (plantInfoList.isEmpty()) {
            throw IllegalStateException("Maaf, saya tidak dapat mengidentifikasi tanaman pada gambar tersebut.")
        }
        val topPlant = plantInfoList.first()

        val userContextPrompt = if (preferences != null) {
            "Konteks Pengguna: Pengguna memiliki pengalaman '${preferences.experience}' dan ketersediaan waktu '${preferences.timeAvailable}'."
        } else ""

        val combinedPrompt = """
            Anda adalah HydroGrow AI, seorang asisten ahli hidroponik.
            $userContextPrompt
            
            Sebuah gambar telah dianalisis dan hasilnya adalah:
            - Nama Umum: ${topPlant.commonName}
            - Nama Ilmiah: ${topPlant.scientificName}
            
            Sekarang, jawab pertanyaan pengguna berikut terkait tanaman ini dengan mempertimbangkan konteks pengguna.
            Pertanyaan Pengguna: "$text"
        """.trimIndent()
        val geminiResponse = aiRepository.getAiAnalysis(combinedPrompt).getOrThrow()
        val imageUrl = imageUploader.uploadImageToStorage(imageFile.toUri(), "chatbot_images", currentUser.uid)
        val userMessage = ChatMessage(role = "user", content = text, imageUrl = imageUrl)
        val aiMessage = ChatMessage(role = "model", content = geminiResponse)

        val titlePrompt = """
            Tugas: Berikan satu judul yang sangat singkat (3-5 kata) untuk sebuah percakapan. Jangan tambahkan tanda kutip atau kata pengantar.
            CONTOH:
            Input: Percakapan tentang cara merawat selada hidroponik.
            Judul: Merawat Selada Hidroponik
            ---
            Input: Percakapan tentang tanaman ${topPlant.commonName}.
            Judul:
        """.trimIndent()
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
        if (addResult is Resource.Success) {
            emit(Resource.Success(newChat.id))
        } else if (addResult is Resource.Error) {
            emit(Resource.Error(addResult.message ?: "Gagal menyimpan chat baru."))
        }
    }.catch { e ->
        emit(Resource.Error("Terjadi kesalahan: ${e.message}"))
    }

    fun startNewConversationWithChosenData(text: String, id: String, contextType: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val currentUser = authRepository.getSignedInUser() ?: throw SecurityException("Pengguna tidak terautentikasi.")

        // [DIUBAH] Ambil preferensi user untuk diteruskan ke pembuat prompt
        val preferences = onboardingRepository.getOnboardingPreferences(currentUser.uid).first()

        val prompt = when (contextType.lowercase()) {
            "garden" -> buildGardenPrompt(id, text, preferences)
            "plant" -> buildPlantPrompt(id, text, preferences)
            else -> Result.failure(IllegalArgumentException("Tipe konteks tidak valid: $contextType"))
        }.getOrThrow()
        val responseText = aiRepository.getAiAnalysis(prompt).getOrThrow()

        val titlePrompt = """
            Tugas: Berikan satu judul yang sangat singkat (3-5 kata) untuk sebuah percakapan. Jangan tambahkan tanda kutip atau kata pengantar.
            CONTOH:
            Input: Percakapan tentang cara merawat selada hidroponik.
            Judul: Merawat Selada Hidroponik
            ---
            Input: Percakapan tentang "$text".
            Judul:
        """.trimIndent()
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
        if (addResult is Resource.Success) {
            emit(Resource.Success(newChat.id))
        } else if (addResult is Resource.Error) {
            emit(Resource.Error(addResult.message ?: "Gagal menyimpan chat baru."))
        }
    }.catch { e ->
        emit(Resource.Error("Gagal memulai percakapan dengan data pilihan: ${e.message}"))
    }

    // [DIUBAH] Menambahkan parameter OnboardingPreferences
    private suspend fun buildGardenPrompt(id: String, userQuestion: String, preferences: OnboardingPreferences?): Result<String> {
        val garden = gardenRepository.getGardenById(id)
        return if (garden != null) {
            val userContextPrompt = if (preferences != null) {
                "Konteks Pengguna: Pengguna memiliki pengalaman '${preferences.experience}'."
            } else ""

            val prompt = """
            $userContextPrompt
            Konteks Taman: Pengguna bertanya tentang taman hidroponik mereka "${garden.gardenName}" dengan tipe ${garden.hydroponicType}.
            Pertanyaan Pengguna: "$userQuestion"
            Jawab pertanyaan tersebut berdasarkan konteks pengguna dan taman yang diberikan.
            """.trimIndent()
            Result.success(prompt)
        } else {
            Result.failure(Exception("Data taman dengan ID $id tidak ditemukan."))
        }
    }

    // [DIUBAH] Menambahkan parameter OnboardingPreferences
    private suspend fun buildPlantPrompt(id: String, userQuestion: String, preferences: OnboardingPreferences?): Result<String> {
        val plant = plantRepository.getPlantById(id).firstOrNull()
        return if (plant != null) {
            val userContextPrompt = if (preferences != null) {
                "Konteks Pengguna: Pengguna memiliki pengalaman '${preferences.experience}'."
            } else ""

            val prompt = """
            $userContextPrompt
            Konteks Tanaman: Pengguna bertanya tentang tanaman "${plant.plantName}" dengan perkiraan panen ${plant.harvestTime}.
            Pertanyaan Pengguna: "$userQuestion"
            Jawab pertanyaan tersebut berdasarkan konteks pengguna dan tanaman yang diberikan.
            """.trimIndent()
            Result.success(prompt)
        } else {
            Result.failure(Exception("Data tanaman dengan ID $id tidak ditemukan."))
        }
    }
}

fun File.toUri(): Uri = Uri.fromFile(this)