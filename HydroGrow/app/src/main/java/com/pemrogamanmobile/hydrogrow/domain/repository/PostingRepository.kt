package com.pemrogamanmobile.hydrogrow.domain.repository

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.domain.model.Comment
import com.pemrogamanmobile.hydrogrow.domain.model.Posting
import kotlinx.coroutines.flow.Flow

/**
 * Antarmuka ini mendefinisikan kontrak untuk operasi terkait data Posting.
 * Ini adalah jembatan antara domain layer (use cases) dan data layer (repository implementation).
 */
interface PostingRepository {

    /**
     * Menyisipkan postingan baru ke sumber data.
     * @param posting Objek Posting yang akan disimpan.
     */
    suspend fun insertPosting(posting: Posting)

    /**
     * Mengunggah gambar untuk sebuah postingan dan mengembalikan URL-nya.
     * @param uri Uri dari gambar yang akan diunggah.
     * @return String URL dari gambar yang telah diunggah.
     */
    suspend fun uploadPostingImage(uri: Uri): String

    /**
     * Memperbarui data postingan yang sudah ada.
     * @param posting Objek Posting dengan data yang telah diperbarui.
     */
    suspend fun updatePosting(posting: Posting)

    /**
     * Menghapus sebuah postingan dari sumber data.
     * @param posting Objek Posting yang akan dihapus.
     */
    suspend fun deletePosting(posting: Posting)

    /**
     * Mendapatkan semua postingan sebagai aliran data (Flow) yang akan otomatis
     * diperbarui saat ada perubahan.
     * @return Flow yang berisi daftar (List) dari semua Posting.
     */
    fun getAllPostings(): Flow<List<Posting>>

    /**
     * Mendapatkan satu postingan spesifik berdasarkan ID-nya.
     * @param postingId ID dari postingan yang dicari.
     * @return Objek Posting jika ditemukan, atau null jika tidak.
     */
    suspend fun getPostingById(postingId: String): Posting?

    /**
     * Mendapatkan semua postingan yang dimiliki oleh pengguna yang sedang login.
     * Tidak memerlukan parameter karena ID pengguna diambil secara internal.
     * @return Flow yang berisi daftar Posting milik pengguna tersebut.
     */
    fun getMyPostings(): Flow<List<Posting>> // Nama diubah agar lebih jelas

    /**
     * Menambahkan komentar baru ke sebuah postingan.
     * @param comment Objek Comment yang akan ditambahkan.
     */
    suspend fun addComment(comment: Comment)

    /**
     * Menghapus sebuah komentar dari sebuah postingan.
     * @param comment Objek Comment yang akan dihapus.
     */
    suspend fun deleteComment(comment: Comment)
}