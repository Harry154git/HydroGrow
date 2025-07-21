package com.pemrogamanmobile.hydrogrow.data.repository

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.ChatBotDao
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toEntity
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDto
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.AuthService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.ImageUploader
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.ChatBotService
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage
import com.pemrogamanmobile.hydrogrow.domain.repository.ChatBotRepository
import com.pemrogamanmobile.hydrogrow.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class ChatBotRepositoryImpl @Inject constructor(
    private val dao: ChatBotDao,
    private val firestore: ChatBotService,
    private val authService: AuthService,
    private val imageUploader: ImageUploader
) : ChatBotRepository {

    override fun getChatHistory(): Flow<Resource<List<ChatBot>>> = flow {
        emit(Resource.Loading())

        val userId = authService.getCurrentUser()?.uid

        // 2. Pastikan userId tidak null atau kosong sebelum melanjutkan
        if (userId.isNullOrBlank()) {
            emit(Resource.Error("Pengguna tidak ditemukan. Silakan login kembali."))
            return@flow // Hentikan eksekusi fungsi
        }

        // Ambil data dari cache lokal terlebih dahulu untuk ditampilkan cepat
        val localHistory = dao.getChatsByUserId(userId).map { entities -> entities.map { it.toDomain() } }
        localHistory.collect { emit(Resource.Success(it)) }

        try {
            // Ambil data dari Firestore (Network-First)
            val remoteHistory = firestore.getChatsByUserId(userId).map { it.toDomain() }
            // Hapus data lama dan simpan yang baru dari network
            dao.deleteAllChatsByUserId(userId)
            dao.insertAllChats(remoteHistory.map { it.toEntity() })
        } catch (e: IOException) {
            emit(Resource.Error("Koneksi internet bermasalah. Menampilkan data offline."))
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan: ${e.message}"))
        }
    }

    override fun getChatById(chatId: String): Flow<Resource<ChatBot>> = flow {
        emit(Resource.Loading())
        try {
            // Ambil data dari database lokal
            val chatEntity = dao.getChatById(chatId)
            if (chatEntity != null) {
                // Jika ditemukan, kirim sebagai data sukses
                emit(Resource.Success(chatEntity.toDomain()))
            } else {
                // Jika tidak ditemukan di lokal, kirim error
                emit(Resource.Error("Percakapan tidak ditemukan."))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan: ${e.message}"))
        }
    }

    override suspend fun addChat(chat: ChatBot): Resource<Unit> {
        return try {
            // Simpan ke local dan remote
            dao.insertChat(chat.toEntity())
            firestore.saveChat(chat.toDto())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal menambahkan percakapan: ${e.message}")
        }
    }

    override suspend fun updateChat(chat: ChatBot): Resource<Unit> {
        return try {
            chat.updatedAt = System.currentTimeMillis() // Perbarui timestamp
            // Update local dan remote
            dao.updateChat(chat.toEntity())
            firestore.saveChat(chat.toDto())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal memperbarui percakapan: ${e.message}")
        }
    }

    override suspend fun addMessageToChat(chatId: String, message: ChatMessage, imageUri: Uri?): Resource<Unit> {
        try {
            val chatEntity = dao.getChatById(chatId) ?: return Resource.Error("Percakapan tidak ditemukan")
            val chat = chatEntity.toDomain()

            // 1. Jika ada gambar, unggah dan buat pesan gambar
            if (imageUri != null) {
                val imageUrl = imageUploader.uploadImageToStorage(imageUri,"chatbot") // Asumsi fungsi ini mengembalikan URL
                val imageMessage = ChatMessage(
                    role = ChatMessage.ROLE_IMAGE,
                    content = imageUrl,
                )
                chat.conversation.add(imageMessage)
            }

            // 2. Tambahkan pesan teks dari pengguna
            chat.conversation.add(message)

            // 3. (Opsional) Dapatkan balasan dari AI dan tambahkan ke percakapan
            // val botResponse = geminiService.generateResponse(chat.conversation)
            // chat.conversation.add(botResponse)

            // 4. Update percakapan di local & remote
            return updateChat(chat)

        } catch (e: Exception) {
            return Resource.Error("Gagal mengirim pesan: ${e.message}")
        }
    }

    override suspend fun deleteChat(chatId: String): Resource<Unit> {
        return try {
            // Hapus dari local dan remote
            dao.deleteChatById(chatId)
            firestore.deleteChat(chatId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal menghapus percakapan: ${e.message}")
        }
    }
}