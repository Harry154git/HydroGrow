package com.pemrogamanmobile.hydrogrow.domain.usecase.ai.chatbot

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.ImageUploader
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage
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
    private val imageUploader: ImageUploader
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

    // ✅ FIX: Fungsi dirombak untuk menangani re-identifikasi gambar
    fun continueConversation(chatbotid: String, message: ChatMessage, imageFile: File?): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        // 1. Jika ada gambar, unggah terlebih dahulu untuk mendapatkan URL
        // DIUBAH: Dapatkan user ID terlebih dahulu.
        // Jika user tidak login saat mengirim gambar, proses akan gagal, ini bagus untuk keamanan.
        val currentUser = authRepository.getSignedInUser()
            ?: throw SecurityException("Pengguna tidak terautentikasi untuk melanjutkan percakapan.")

        // 1. Jika ada gambar, unggah terlebih dahulu untuk mendapatkan URL
        val imageUrl = if (imageFile != null) {
            // DIUBAH: Sertakan currentUser.uid saat memanggil imageUploader
            imageUploader.uploadImageToStorage(imageFile.toUri(), "chatbot_images", currentUser.uid)
        } else {
            null
        }

        // 2. Buat pesan pengguna yang final dengan imageUrl jika ada
        val finalUserMessage = message.copy(imageUrl = imageUrl)

        // 3. Tambahkan pesan pengguna ke percakapan
        val addUserMessageResult = chatRepository.addMessageToChat(chatbotid, finalUserMessage, null)
        if (addUserMessageResult is Resource.Error) {
            emit(addUserMessageResult)
            return@flow
        }

        // 4. Buat prompt untuk Gemini.
        val prompt: String
        if (imageFile != null) {
            // Jika ada gambar baru, identifikasi lagi!
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
            // Jika hanya teks, gunakan histori sebelumnya
            val chatResource = chatRepository.getChatById(chatbotid).first { it !is Resource.Loading }
            val chat = (chatResource as? Resource.Success)?.data
                ?: throw IllegalStateException("Gagal memuat data percakapan.")
            // Membuat prompt lebih sederhana untuk menghindari token limit
            prompt = "Konteks riwayat percakapan sudah diberikan. Jawab pertanyaan terakhir dari pengguna: \"${message.content}\""
        }

        // 5. Dapatkan respons dari Gemini dan simpan
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
        val responseText = aiRepository.getAiAnalysis(text).getOrThrow()

        // ✅ FIX: Prompt judul yang lebih baik
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
        val plantInfoList = aiRepository.identifyPlant(imageFile, "auto").getOrThrow()
        if (plantInfoList.isEmpty()) {
            throw IllegalStateException("Maaf, saya tidak dapat mengidentifikasi tanaman pada gambar tersebut.")
        }
        val topPlant = plantInfoList.first()
        val combinedPrompt = """
            Anda adalah HydroGrow AI, seorang asisten ahli hidroponik.
            Sebuah gambar telah dianalisis oleh sistem identifikasi tanaman dan hasilnya adalah:
            - Nama Umum: ${topPlant.commonName}
            - Nama Ilmiah: ${topPlant.scientificName}
            - Tingkat Keyakinan: ${(topPlant.score * 100).toInt()}%
            Sekarang, jawab pertanyaan pengguna berikut yang berkaitan dengan tanaman ini. Berikan jawaban yang informatif dan relevan dengan hidroponik jika memungkinkan.
            Pertanyaan Pengguna: "$text"
        """.trimIndent()
        val geminiResponse = aiRepository.getAiAnalysis(combinedPrompt).getOrThrow()
        val imageUrl = imageUploader.uploadImageToStorage(imageFile.toUri(), "chatbot_images", currentUser.uid)
        val userMessage = ChatMessage(role = "user", content = text, imageUrl = imageUrl)
        val aiMessage = ChatMessage(role = "model", content = geminiResponse)

        // ✅ FIX: Prompt judul yang lebih baik
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
        val prompt = when (contextType.lowercase()) {
            "garden" -> buildGardenPrompt(id, text)
            "plant" -> buildPlantPrompt(id, text)
            else -> Result.failure(IllegalArgumentException("Tipe konteks tidak valid: $contextType"))
        }.getOrThrow()
        val responseText = aiRepository.getAiAnalysis(prompt).getOrThrow()

        // ✅ FIX: Prompt judul yang lebih baik
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

fun File.toUri(): Uri = Uri.fromFile(this)