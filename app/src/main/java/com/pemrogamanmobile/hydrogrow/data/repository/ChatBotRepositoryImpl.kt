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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.onStart

class ChatBotRepositoryImpl @Inject constructor(
    private val dao: ChatBotDao,
    private val firestore: ChatBotService,
    private val authService: AuthService,
    private val imageUploader: ImageUploader
) : ChatBotRepository {

    // ✅ FIX: Menerapkan pola Single Source of Truth
    // Fungsi ini HANYA mengembalikan data dari database lokal sebagai satu-satunya sumber kebenaran.
    override fun getChatHistory(): Flow<List<ChatBot>> {
        val userId = authService.getCurrentUser()?.uid ?: return flow { emptyList<ChatBot>() }
        return dao.getChatsByUserId(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // ✅ BARU: Fungsi suspend terpisah untuk sinkronisasi dari jaringan
    // ViewModel akan memanggil fungsi ini untuk memicu pembaruan data.
    override suspend fun refreshChatHistory(): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = authService.getCurrentUser()?.uid
                if (userId.isNullOrBlank()) {
                    return@withContext Resource.Error("Pengguna tidak ditemukan.")
                }

                // Ambil data dari Firestore
                val remoteHistory = firestore.getChatsByUserId(userId).map { it.toDomain() }

                // Hapus data lama dan simpan yang baru dari network
                dao.deleteAllChatsByUserId(userId)
                dao.insertAllChats(remoteHistory.map { it.toEntity() })
                Resource.Success(Unit)
            } catch (e: IOException) {
                Resource.Error("Koneksi internet bermasalah.")
            } catch (e: Exception) {
                Resource.Error("Gagal sinkronisasi data: ${e.message}")
            }
        }
    }


    override fun getChatById(chatId: String): Flow<Resource<ChatBot>> {
        // 1. Mulai dengan Flow real-time dari Firestore
        return firestore.getChatByIdRealtime(chatId) // Ini adalah Flow<ChatBotDto?>
            .map { remoteChatDto ->
                // 2. Operator 'map' mengubah setiap item yang diterima dari Flow.
                //    Di sini kita ubah ChatBotDto? menjadi Resource<ChatBot>
                if (remoteChatDto != null) {
                    // Jika data dari Firestore ada
                    val remoteChat = remoteChatDto.toDomain()
                    dao.insertChat(remoteChat.toEntity()) // Simpan/update cache lokal
                    Resource.Success(remoteChat) as Resource<ChatBot> // Kirim data sukses
                } else {
                    // Jika data dari Firestore null (tidak ada atau dihapus)
                    dao.deleteChatById(chatId) // Bersihkan cache lokal jika perlu
                    Resource.Error<ChatBot>("Percakapan tidak ditemukan atau telah dihapus.")
                }
            }
            .onStart { emit(Resource.Loading()) } // 3. Emit status Loading di awal sebelum data pertama datang
            .catch { e ->
                // 4. Tangkap semua error dari proses di atas (misal: masalah jaringan)
                emit(Resource.Error("Gagal mengambil data: ${e.message}"))
            }
    }

    override suspend fun addChat(chat: ChatBot): Resource<Unit> {
        return try {
            firestore.saveChat(chat.toDto())
            dao.insertChat(chat.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal menambahkan percakapan: ${e.message}")
        }
    }

    override suspend fun updateChat(chat: ChatBot): Resource<Unit> {
        return try {
            chat.updatedAt = System.currentTimeMillis()
            firestore.saveChat(chat.toDto())
            dao.updateChat(chat.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal memperbarui percakapan: ${e.message}")
        }
    }

    // Tidak ada perubahan signifikan di sini, sudah cukup baik.
    override suspend fun addMessageToChat(chatId: String, message: ChatMessage, imageUri: Uri?): Resource<Unit> {
        try {
            val chatEntity = dao.getChatById(chatId) ?: return Resource.Error("Percakapan tidak ditemukan")
            val chat = chatEntity.toDomain()

            if (imageUri != null) {
                val imageUrl = imageUploader.uploadImageToStorage(imageUri, "chatbot")
                val imageMessage = ChatMessage(role = ChatMessage.ROLE_IMAGE, content = imageUrl)
                chat.conversation.add(imageMessage)
            }
            chat.conversation.add(message)
            return updateChat(chat)
        } catch (e: Exception) {
            return Resource.Error("Gagal mengirim pesan: ${e.message}")
        }
    }

    override suspend fun deleteChat(chatId: String): Resource<Unit> {
        return try {
            firestore.deleteChat(chatId)
            dao.deleteChatById(chatId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Gagal menghapus percakapan: ${e.message}")
        }
    }
}