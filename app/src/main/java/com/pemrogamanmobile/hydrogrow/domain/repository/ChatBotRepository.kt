package com.pemrogamanmobile.hydrogrow.domain.repository

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage
import com.pemrogamanmobile.hydrogrow.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface untuk ChatBotRepository.
 *
 * Mendefinisikan kontrak untuk semua operasi data yang terkait dengan fitur chatbot.
 * Interface ini berada di dalam Domain Layer dan tidak memiliki pengetahuan tentang
 * implementasi teknis seperti Firebase atau Room.
 */
interface ChatBotRepository {

    /**
     * Mengambil riwayat percakapan dari sumber data utama (cache/database lokal).
     *
     * @return Sebuah Flow yang akan secara reaktif mengirim daftar percakapan
     * setiap kali ada perubahan pada data.
     */
    fun getChatHistory(): Flow<List<ChatBot>>

    /**
     * Memicu pembaruan/sinkronisasi data riwayat percakapan dari jaringan (network).
     * Hasil dari pembaruan ini akan secara otomatis terpancar melalui Flow dari `getChatHistory()`.
     *
     * @return Resource yang menandakan status dari operasi sinkronisasi (Success/Error).
     */
    suspend fun refreshChatHistory(): Resource<Unit>

    /**
     * Mengambil satu data percakapan spesifik berdasarkan ID-nya.
     *
     * @param chatId ID unik dari percakapan yang dicari.
     * @return Sebuah Flow yang berisi Resource dari objek ChatBot.
     */
    fun getChatById(chatId: String): Flow<Resource<ChatBot>>

    /**
     * Menambahkan percakapan baru ke sumber data.
     *
     * @param chat Objek ChatBot yang akan ditambahkan.
     * @return Resource yang menandakan status operasi penambahan.
     */
    suspend fun addChat(chat: ChatBot): Resource<Unit>

    /**
     * Memperbarui data percakapan yang sudah ada.
     *
     * @param chat Objek ChatBot dengan data yang sudah diperbarui.
     * @return Resource yang menandakan status operasi pembaruan.
     */
    suspend fun updateChat(chat: ChatBot): Resource<Unit>

    /**
     * Menambahkan sebuah pesan baru ke dalam percakapan yang sudah ada.
     *
     * @param chatId ID dari percakapan target.
     * @param message Objek ChatMessage yang akan ditambahkan.
     * @param imageUri (Opsional) URI dari gambar yang mungkin diunggah bersama pesan.
     * @return Resource yang menandakan status operasi.
     */
    suspend fun addMessageToChat(chatId: String, message: ChatMessage, imageUri: Uri?): Resource<Unit>

    /**
     * Menghapus sebuah percakapan dari sumber data.
     *
     * @param chatId ID unik dari percakapan yang akan dihapus.
     * @return Resource yang menandakan status operasi penghapusan.
     */
    suspend fun deleteChat(chatId: String): Resource<Unit>
}