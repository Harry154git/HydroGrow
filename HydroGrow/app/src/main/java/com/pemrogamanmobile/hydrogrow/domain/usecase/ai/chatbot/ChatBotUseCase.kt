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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

class ChatUseCase @Inject constructor(
    private val chatRepository: ChatBotRepository,
    private val aiRepository: AiRepository,
    private val gardenRepository: GardenRepository,
    private val plantRepository: PlantRepository,
    private val authRepository: AuthRepository
) {
    /**
     * Mengambil seluruh riwayat percakapan chatbot milik pengguna saat ini.
     * Data diambil dari cache lokal terlebih dahulu, kemudian diperbarui dari jaringan.
     */
    fun getChatHistory(): Flow<Resource<List<ChatBot>>> {
        return chatRepository.getChatHistory()
    }

    /**
     * Mengambil data percakapan spesifik berdasarkan ID-nya.
     */
    fun getChatBotById(chatbotid: String): Flow<Resource<ChatBot>> {
        return chatRepository.getChatById(chatbotid)
    }

    /**
     * Melanjutkan percakapan yang sudah ada.
     * Mengambil riwayat, menambahkan pesan baru, mendapatkan respons AI, lalu menyimpan pembaruan.
     */
    fun continueConversation(chatbotid: String, text: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            // 1. Ambil data chat terakhir dari flow
            val chatResource = chatRepository.getChatById(chatbotid).first { it !is Resource.Loading }

            if (chatResource is Resource.Success) {
                val chat = chatResource.data // Variabel 'chat' memiliki tipe ChatBot? (nullable)

                // ✅ Pengecekan null yang direkomendasikan
                if (chat != null) {
                    // Di dalam blok ini, 'chat' dianggap non-nullable. Kedua error teratasi.

                    // 2. Tambahkan pesan baru dari pengguna
                    chat.conversation.add(ChatMessage(role = "user", content = text, timestamp = System.currentTimeMillis()))

                    // 3. Buat prompt dari seluruh riwayat percakapan
                    val historyPrompt = chat.conversation.joinToString("\n") { "${it.role}: ${it.content}" }
                    val aiResult = aiRepository.getAiAnalysis(historyPrompt)

                    aiResult.onSuccess { responseText ->
                        // 4. Tambahkan respons dari AI
                        val aiMessage = ChatMessage(role = "model", content = responseText, timestamp = System.currentTimeMillis())
                        chat.conversation.add(aiMessage)

                        // 5. Simpan percakapan yang telah diperbarui (sekarang aman)
                        val updateResult = chatRepository.updateChat(chat)
                        emit(updateResult)
                    }.onFailure { exception ->
                        emit(Resource.Error("Gagal mendapatkan respons dari AI: ${exception.message}"))
                    }
                } else {
                    // Menangani kasus jika resource success tetapi datanya null
                    emit(Resource.Error("Gagal memuat data percakapan."))
                }
            } else {
                // Menangani kasus jika Resource bukan Success (contoh: Resource.Error)
                emit(Resource.Error(chatResource.message ?: "Gagal memuat percakapan."))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan: ${e.message}"))
        }
    }

    /**
     * Memulai percakapan baru hanya dengan teks.
     * Membuat ID baru, mendapatkan respons AI, membuat judul, dan menyimpan semuanya.
     */
    fun startNewConversation(text: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        // 1. Dapatkan ID pengguna yang sedang login
        val currentUser = authRepository.getSignedInUser()
        if (currentUser == null) {
            emit(Resource.Error("Pengguna tidak terautentikasi. Silakan login kembali."))
            return@flow // Hentikan eksekusi jika tidak ada user
        }

        try {
            // 2. Dapatkan respons pertama dari AI
            val aiResult = aiRepository.getAiAnalysis(text)

            aiResult.onSuccess { responseText ->
                val userMessage = ChatMessage(role = "user", content = text, timestamp = System.currentTimeMillis())
                val aiMessage = ChatMessage(role = "model", content = responseText, timestamp = System.currentTimeMillis())

                val titlePrompt = "Buat judul singkat (maksimal 5 kata) untuk percakapan ini:\nUser: $text\nAI: $responseText"
                val titleResult = aiRepository.getAiAnalysis(titlePrompt)

                titleResult.onSuccess { title ->
                    // 3. Buat objek ChatBot baru dengan userOwnerId
                    val newChat = ChatBot(
                        id = UUID.randomUUID().toString(),
                        userOwnerId = currentUser.uid, // <-- FIX: Tambahkan ID pemilik
                        title = title.trim().removeSurrounding("\""),
                        conversation = mutableListOf(userMessage, aiMessage),
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )

                    val addResult = chatRepository.addChat(newChat)
                    emit(addResult)

                }.onFailure { exception ->
                    emit(Resource.Error("Gagal membuat judul: ${exception.message}"))
                }
            }.onFailure { exception ->
                emit(Resource.Error("Gagal mendapatkan respons dari AI: ${exception.message}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan: ${e.message}"))
        }
    }

    /**
     * Memulai percakapan baru dengan deteksi gambar.
     * Mengidentifikasi tanaman dari gambar, menggabungkannya dengan teks, lalu memulai percakapan.
     * Catatan: Konversi dari Uri ke File harus dilakukan di luar use case ini (misal: di ViewModel).
     */
    fun startNewConversationWithImageDetection(text: String, imageFile: File): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        // 1. Dapatkan ID pengguna yang sedang login
        val currentUser = authRepository.getSignedInUser()
        if (currentUser == null) {
            emit(Resource.Error("Pengguna tidak terautentikasi. Silakan login kembali."))
            return@flow // Hentikan eksekusi jika tidak ada user
        }

        try {
            val identificationResult = aiRepository.identifyPlant(imageFile, "auto")

            identificationResult.onSuccess { plantInfoList ->
                if (plantInfoList.isEmpty()) {
                    emit(Resource.Error("Tanaman tidak dapat diidentifikasi."))
                    return@flow
                }
                val topPlant = plantInfoList.first()
                val combinedPrompt = """...""" // Prompt Anda di sini

                val aiResult = aiRepository.getAiAnalysis(combinedPrompt)

                aiResult.onSuccess { responseText ->
                    val finalResponse = "Ini adalah tanaman: **${topPlant.commonName}** (*${topPlant.scientificName}*).\n\n$responseText"
                    val userMessage = ChatMessage(role = "user", content = text, timestamp = System.currentTimeMillis())
                    val aiMessage = ChatMessage(role = "model", content = finalResponse, timestamp = System.currentTimeMillis())
                    val titlePrompt = "Buat judul singkat (maksimal 5 kata) untuk percakapan tentang tanaman ${topPlant.commonName} ini."
                    val titleResult = aiRepository.getAiAnalysis(titlePrompt)

                    titleResult.onSuccess { title ->
                        val newChat = ChatBot(
                            id = UUID.randomUUID().toString(),
                            userOwnerId = currentUser.uid, // <-- FIX: Tambahkan ID pemilik
                            title = title.trim().removeSurrounding("\""),
                            conversation = mutableListOf(userMessage, aiMessage),
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        val addResult = chatRepository.addChat(newChat)
                        emit(addResult)
                    }.onFailure { emit(Resource.Error("Gagal membuat judul: ${it.message}")) }
                }.onFailure { emit(Resource.Error("Gagal mendapatkan respons AI: ${it.message}")) }
            }.onFailure { emit(Resource.Error("Gagal mengidentifikasi tanaman: ${it.message}")) }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan: ${e.message}"))
        }
    }

    /**
     * Memulai percakapan baru berdasarkan data yang ada (Taman atau Tanaman).
     * @param text Pertanyaan dari pengguna.
     * @param id ID dari Garden atau Plant.
     * @param contextType Tipe dari data, "garden" atau "plant".
     */
    fun startNewConversationWithChosenData(text: String, id: String, contextType: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        // 1. Dapatkan ID pengguna yang sedang login
        val currentUser = authRepository.getSignedInUser()
        if (currentUser == null) {
            emit(Resource.Error("Pengguna tidak terautentikasi. Silakan login kembali."))
            return@flow // Hentikan eksekusi jika tidak ada user
        }

        try {
            val contextPromptResult = when (contextType.lowercase()) {
                "garden" -> buildGardenPrompt(id, text)
                "plant" -> buildPlantPrompt(id, text)
                else -> Result.failure(IllegalArgumentException("Tipe konteks tidak valid: $contextType"))
            }

            contextPromptResult.onSuccess { prompt ->
                val aiResult = aiRepository.getAiAnalysis(prompt)

                aiResult.onSuccess { responseText ->
                    val userMessage = ChatMessage(role = "user", content = text, timestamp = System.currentTimeMillis())
                    val aiMessage = ChatMessage(role = "model", content = responseText, timestamp = System.currentTimeMillis())
                    val titlePrompt = "Buat judul singkat (maksimal 5 kata) untuk percakapan ini:\nUser: $text\nAI: $responseText"
                    val titleResult = aiRepository.getAiAnalysis(titlePrompt)

                    titleResult.onSuccess { title ->
                        val newChat = ChatBot(
                            id = UUID.randomUUID().toString(),
                            userOwnerId = currentUser.uid, // <-- FIX: Tambahkan ID pemilik
                            title = title.trim().removeSurrounding("\""),
                            conversation = mutableListOf(userMessage, aiMessage),
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        val addResult = chatRepository.addChat(newChat)
                        emit(addResult)
                    }.onFailure { emit(Resource.Error("Gagal membuat judul: ${it.message}")) }
                }.onFailure { emit(Resource.Error("Gagal mendapatkan respons AI: ${it.message}")) }
            }.onFailure { emit(Resource.Error("Gagal membuat prompt konteks: ${it.message}")) }

        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan: ${e.message}"))
        }
    }

    // Helper function untuk membuat prompt taman
    private suspend fun buildGardenPrompt(id: String, userQuestion: String): Result<String> {
        // getGardenById mengembalikan Garden?, bukan Resource.
        val garden = gardenRepository.getGardenById(id)

        // Cukup periksa apakah garden null atau tidak.
        return if (garden != null) {
            val prompt = """
            Konteks: Pengguna bertanya tentang taman hidroponik mereka yang bernama "${garden.gardenName}".
            Detail Taman: Tipe hidroponik ${garden.hydroponicType} dengan ukuran ${garden.gardenSize} meter persegi.
            
            Pertanyaan Pengguna: "$userQuestion"
            
            Jawab pertanyaan tersebut berdasarkan konteks taman yang diberikan.
        """.trimIndent()
            Result.success(prompt)
        } else {
            // Jika garden null, berarti data tidak ditemukan.
            Result.failure(Exception("Data taman dengan ID $id tidak ditemukan."))
        }
    }

    // Helper function untuk membuat prompt tanaman
    private suspend fun buildPlantPrompt(id: String, userQuestion: String): Result<String> {
        // getPlantById mengembalikan Flow<Plant?>.
        // Kita ambil item pertama dari flow tersebut.
        val plant = plantRepository.getPlantById(id).firstOrNull()

        // Sama seperti garden, cukup periksa apakah plant null.
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