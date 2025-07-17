package com.pemrogamanmobile.hydrogrow.data.repository

import android.net.Uri
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
    private val dao: PostingDao,
    private val firestore: PostingService,
    private val authService: AuthService,
    private val imageUploader: ImageUploader
) : PostingRepository {

    private fun getUserId(): String {
        return authService.getCurrentUser()?.uid.orEmpty()
    }

    override suspend fun insertPosting(posting: Posting) {
        firestore.uploadPosting(posting.toDto())
        dao.insertPosting(posting.toEntity())
    }

    override suspend fun uploadPostingImage(uri: Uri): String {
        return imageUploader.uploadImageToStorage(uri, "postings")
    }

    override suspend fun updatePosting(posting: Posting) {
        firestore.uploadPosting(posting.toDto())
        dao.updatePosting(posting.toEntity())
    }

    override suspend fun deletePosting(posting: Posting) {
        firestore.deletePosting(posting.id)
        dao.deletePosting(posting.toEntity())
    }

    override fun getAllPostings(): Flow<List<Posting>> {
        return createSyncedPostingFlow()
    }

    private fun createSyncedPostingFlow(): Flow<List<Posting>> = flow {
        try {
            val remoteData = firestore.getAllPostings().map { it.toDomain() }
            dao.deleteAllPostings()
            dao.insertPostings(remoteData.map { it.toEntity() })
        } catch (e: Exception) {
            // Log error jika perlu
        }
        emitAll(dao.getAllPostings().map { entities ->
            entities.map { it.toDomain() }
        })
    }

    /**
     * Implementasi untuk mengambil postingan milik pengguna yang sedang login.
     */
    override fun getMyPostings(): Flow<List<Posting>> {
        // Di sinilah getUserId() yang sebelumnya tidak terpakai akhirnya digunakan!
        val userId = getUserId()
        return createSyncedUserPostingFlow(userId)
    }

    /**
     * Helper function untuk sinkronisasi postingan milik satu user saja.
     * Fungsi ini tetap sama, menerima userId sebagai parameter.
     */
    private fun createSyncedUserPostingFlow(userId: String): Flow<List<Posting>> = flow {
        if (userId.isNotEmpty()) {
            try {
                // 1. Ambil data terbaru dari Firestore untuk user ini
                val remoteData = firestore.getPostingsByUserId(userId).map { it.toDomain() }

                // 2. Masukkan data BARU dari Firestore ke lokal
                // Ini akan otomatis menggantikan data lama karena OnConflictStrategy.REPLACE
                dao.insertPostings(remoteData.map { it.toEntity() })

            } catch (e: Exception) {
                // Jika gagal (offline), akan lanjut mengandalkan cache lokal
            }
        }

        // 3. Emit semua data dari Room untuk user ini dan pantau perubahannya
        emitAll(dao.getPostingByUserId(userId).map { entities ->
            entities.map { it.toDomain() }
        })
    }

    override suspend fun getPostingById(postingId: String): Posting? {
        return try {
            val remotePostingDto = firestore.getPostingById(postingId)
            if (remotePostingDto != null) {
                val posting = remotePostingDto.toDomain()
                dao.insertPosting(posting.toEntity())
                posting
            } else {
                dao.deletePostingById(postingId)
                null
            }
        } catch (e: Exception) {
            dao.getPostingById(postingId)?.toDomain()
        }
    }

    // --- Manajemen Komentar (Versi Lebih Robust) ---

    override suspend fun addComment(comment: Comment) {
        firestore.addComment(comment.postId, comment.toDto())
        try {
            val updatedPosting = firestore.getPostingById(comment.postId)?.toDomain()
            if (updatedPosting != null) {
                dao.insertPosting(updatedPosting.toEntity())
            }
        } catch (e: Exception) {
            // Gagal sinkronisasi setelah menambah komen
        }
    }

    override suspend fun deleteComment(comment: Comment) {
        firestore.deleteComment(comment.postId, comment.toDto())
        try {
            val updatedPosting = firestore.getPostingById(comment.postId)?.toDomain()
            if (updatedPosting != null) {
                dao.insertPosting(updatedPosting.toEntity())
            }
        } catch (e: Exception) {
            // Gagal sinkronisasi setelah menghapus komen
        }
    }
}