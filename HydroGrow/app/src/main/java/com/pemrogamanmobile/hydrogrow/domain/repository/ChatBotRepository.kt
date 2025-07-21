package com.pemrogamanmobile.hydrogrow.domain.repository

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.domain.model.ChatBot
import com.pemrogamanmobile.hydrogrow.domain.model.ChatMessage
import com.pemrogamanmobile.hydrogrow.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatBotRepository {

    /**
     * Mengambil seluruh riwayat percakapan untuk pengguna saat ini.
     * Menerapkan strategi cache network-first.
     * @return Flow yang memancarkan status loading, error, atau sukses dengan daftar ChatBot.
     */
    fun getChatHistory(): Flow<Resource<List<ChatBot>>>

    // Di dalam interface ChatBotRepository
    fun getChatById(chatId: String): Flow<Resource<ChatBot>>

    /**
     * Menambahkan sesi percakapan baru ke database lokal dan Firestore.
     * @param chat Objek ChatBot yang akan dibuat.
     * @return Resource yang menandakan sukses atau gagal.
     */
    suspend fun addChat(chat: ChatBot): Resource<Unit>

    /**
     * Memperbarui sesi percakapan yang sudah ada.
     * @param chat Objek ChatBot dengan data yang sudah diperbarui.
     * @return Resource yang menandakan sukses atau gagal.
     */
    suspend fun updateChat(chat: ChatBot): Resource<Unit>

    /**
     * Menambahkan pesan baru (teks dan/atau gambar) ke sebuah sesi percakapan.
     * @param chatId ID dari percakapan yang akan ditambahkan pesannya.
     * @param message Objek pesan teks dari pengguna.
     * @param imageUri (Opsional) URI dari gambar yang akan diunggah.
     * @return Resource yang menandakan sukses atau gagal.
     */
    suspend fun addMessageToChat(chatId: String, message: ChatMessage, imageUri: Uri?): Resource<Unit>

    /**
     * Menghapus sebuah sesi percakapan dari database lokal dan Firestore.
     * @param chatId ID dari percakapan yang akan dihapus.
     * @return Resource yang menandakan sukses atau gagal.
     */
    suspend fun deleteChat(chatId: String): Resource<Unit>
}