package com.pemrogamanmobile.hydrogrow.data.repository

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.CommentDao
import com.pemrogamanmobile.hydrogrow.data.local.room.dao.PostingDao
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.local.mapper.toEntity
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.data.remote.mapper.toDto
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.AuthService
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.ImageUploader
import com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices.PostingService
import com.pemrogamanmobile.hydrogrow.domain.model.Comment
import com.pemrogamanmobile.hydrogrow.domain.model.Posting
import com.pemrogamanmobile.hydrogrow.domain.repository.PostingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PostingRepositoryImpl @Inject constructor(
    // DAO sekarang dipisah agar lebih jelas
    private val postingDao: PostingDao,
    private val commentDao: CommentDao, // <-- DEPENDENSI BARU
    private val firestore: PostingService,
    private val authService: AuthService,
    private val imageUploader: ImageUploader
) : PostingRepository {

    private fun getUserId(): String {
        return authService.getCurrentUser()?.uid.orEmpty()
    }

    override suspend fun uploadPostingImage(uri: Uri): String {
        return imageUploader.uploadImageToStorage(uri, "postings")
    }

    // --- Operasi CRUD Postingan ---

    override suspend fun insertPosting(posting: Posting) {
        // 1. Unggah ke Firestore
        firestore.uploadPosting(posting.toDto())

        // 2. Simpan postingan dan komentarnya ke Room
        postingDao.insertPosting(posting.toEntity())
        commentDao.insertAll(posting.comments.toEntity())
    }

    override suspend fun updatePosting(posting: Posting) {
        // 1. Perbarui di Firestore
        firestore.uploadPosting(posting.toDto())

        // 2. Perbarui postingan dan komentarnya di Room
        postingDao.updatePosting(posting.toEntity())
        // Hapus komentar lama dan masukkan yang baru untuk memastikan sinkronisasi
        commentDao.deleteCommentsByPostId(posting.id)
        commentDao.insertAll(posting.comments.toEntity())
    }

    override suspend fun deletePosting(posting: Posting) {
        // 1. Hapus dari Firestore
        firestore.deletePosting(posting.id)

        // 2. Hapus dari Room. Komentar akan terhapus otomatis karena `onDelete = CASCADE`
        postingDao.deletePosting(posting.toEntity())
    }

    // --- Operasi Pengambilan Data (Flows) ---

    override fun getAllPostings(): Flow<List<Posting>> {
        // Fungsi ini tetap sama, untuk feed umum
        return createSyncedPostingFlow()
    }

    private fun createSyncedPostingFlow(): Flow<List<Posting>> = flow {
        try {
            // 1. Ambil data postingan terbaru dari Firestore
            val remoteData = firestore.getAllPostings().map { it.toDomain() }
            val allComments = remoteData.flatMap { it.comments } // Ambil semua komentar dari semua postingan

            // 2. Bersihkan cache lokal
            postingDao.deleteAllPostings()
            commentDao.deleteAllComments() // <-- HAPUS SEMUA KOMENTAR JUGA

            // 3. Masukkan data baru yang bersih ke Room
            postingDao.insertPostings(remoteData.map { it.toEntity() })
            commentDao.insertAll(allComments.toEntity()) // <-- SIMPAN SEMUA KOMENTAR

        } catch (e: Exception) {
            // Jika gagal (offline), akan lanjut mengandalkan cache lokal yang ada
        }

        // 4. Emit data dari Room dan pantau perubahannya
        // Di sini Anda memerlukan query @Transaction di DAO untuk menggabungkan postingan dan komentar
        emitAll(postingDao.getPostingsWithComments().map { entities ->
            entities.map { it.toDomain() }
        })
    }

    override fun getMyPostings(): Flow<List<Posting>> {
        val userId = getUserId()
        // Panggil flow khusus yang menerima userId
        return createSyncedUserPostingFlow(userId)
    }

    /**
     * FUNGSI HELPER BARU:
     * Membuat flow yang menyinkronkan dan mengambil data postingan
     * untuk SATU pengguna spesifik.
     */
    private fun createSyncedUserPostingFlow(userId: String): Flow<List<Posting>> = flow {
        // Hanya jalankan sinkronisasi jika userId tidak kosong
        if (userId.isNotEmpty()) {
            try {
                // 1. Ambil data terbaru dari Firestore HANYA untuk user ini
                val remoteUserPosts = firestore.getPostingsByUserId(userId).map { it.toDomain() }
                val userComments = remoteUserPosts.flatMap { it.comments }

                // 2. Masukkan data BARU dari Firestore ke lokal.
                // OnConflictStrategy.REPLACE akan otomatis memperbarui data lama.
                // Ini lebih efisien daripada menghapus semua postingan.
                postingDao.insertPostings(remoteUserPosts.map { it.toEntity() })
                commentDao.insertAll(userComments.toEntity())

            } catch (e: Exception) {
                // Jika gagal (misal, offline), flow akan lanjut mengandalkan cache lokal
            }
        }

        // 3. Emit semua data dari Room HANYA untuk user ini dan pantau perubahannya.
        // Memanggil fungsi DAO yang sudah kita revisi sebelumnya.
        emitAll(postingDao.getPostingsWithCommentsByUserId(userId).map { entities ->
            entities.map { it.toDomain() }
        })
    }

    override suspend fun getPostingById(postingId: String): Posting? {
        return try {
            val remotePostingDto = firestore.getPostingById(postingId)
            if (remotePostingDto != null) {
                val posting = remotePostingDto.toDomain()
                // Update postingan dan komentarnya di lokal
                postingDao.insertPosting(posting.toEntity())
                commentDao.deleteCommentsByPostId(posting.id)
                commentDao.insertAll(posting.comments.toEntity())
                posting
            } else {
                postingDao.deletePostingById(postingId) // Juga akan menghapus komentar via CASCADE
                null
            }
        } catch (e: Exception) {
            // Ambil dari lokal jika offline. Memerlukan query @Transaction.
            postingDao.getPostingWithCommentsById(postingId)?.toDomain()
        }
    }

    // --- Manajemen Komentar (Logika yang Sudah Benar) ---

    override suspend fun addComment(comment: Comment) {
        // 1. Tambahkan di Firestore
        firestore.addComment(comment.postId, comment.toDto())
        try {
            // 2. Ambil postingan yang sudah ter-update dari Firestore
            val updatedPosting = firestore.getPostingById(comment.postId)?.toDomain()
            if (updatedPosting != null) {
                // 3. Perbarui postingan dan daftar komentarnya di Room
                postingDao.insertPosting(updatedPosting.toEntity())
                commentDao.deleteCommentsByPostId(updatedPosting.id) // Hapus yang lama
                commentDao.insertAll(updatedPosting.comments.toEntity()) // Masukkan list baru
            }
        } catch (e: Exception) {
            // Gagal sinkronisasi setelah menambah komen
        }
    }

    override suspend fun deleteComment(comment: Comment) {
        // 1. Hapus dari Firestore
        firestore.deleteComment(comment.postId, comment.toDto())
        try {
            // 2. Ambil postingan yang sudah ter-update dari Firestore
            val updatedPosting = firestore.getPostingById(comment.postId)?.toDomain()
            if (updatedPosting != null) {
                // 3. Perbarui postingan dan daftar komentarnya di Room
                postingDao.insertPosting(updatedPosting.toEntity())
                commentDao.deleteCommentsByPostId(updatedPosting.id) // Hapus yang lama
                commentDao.insertAll(updatedPosting.comments.toEntity()) // Masukkan list baru
            }
        } catch (e: Exception) {
            // Gagal sinkronisasi setelah menghapus komen
        }
    }

    /**
     * Implementasi untuk like/unlike postingan.
     */
    override suspend fun likeUnlikePosting(postId: String) {
        val userId = getUserId()
        if (userId.isEmpty()) return // Jangan lakukan apa-apa jika user tidak login

        // Asumsi: firestore service Anda punya fungsi untuk menangani sub-koleksi 'likes'
        // Anda perlu membuat fungsi-fungsi ini di dalam PostingService Anda.

        // 1. Cek di Firestore apakah user sudah me-like postingan ini
        val isAlreadyLiked = firestore.isPostLikedByUser(postId, userId)

        if (isAlreadyLiked) {
            // --- JIKA SUDAH LIKE (AKSI: UNLIKE) ---

            // 2a. Hapus 'like' dari Firestore
            firestore.unlikePost(postId, userId)

            // 3a. Update jumlah like di Room (berkurang 1) secara lokal
            val currentPost = postingDao.getPostingById(postId) // Perlu fungsi ini di DAO
            currentPost?.let {
                val newLikesCount = (it.likes - 1).coerceAtLeast(0) // Cegah nilai negatif
                postingDao.updateLikesCount(postId, newLikesCount) // Perlu fungsi ini di DAO
            }

        } else {
            // --- JIKA BELUM LIKE (AKSI: LIKE) ---

            // 2b. Tambahkan 'like' ke Firestore
            firestore.likePost(postId, userId)

            // 3b. Update jumlah like di Room (bertambah 1) secara lokal
            val currentPost = postingDao.getPostingById(postId)
            currentPost?.let {
                val newLikesCount = it.likes + 1
                postingDao.updateLikesCount(postId, newLikesCount)
            }
        }

        // Aksi like/unlike di Firestore akan memicu Cloud Function untuk mengirim notifikasi.
        // Cache di Room diupdate secara manual agar UI langsung responsif tanpa perlu fetch ulang.
    }
}