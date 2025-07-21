package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.remote.dto.CommentDto
import com.pemrogamanmobile.hydrogrow.data.remote.dto.PostingDto
import kotlinx.coroutines.tasks.await

class PostingService(private val db: FirebaseFirestore) {

    private val postingCol = db.collection("postings")

    /**
     * Mengambil semua postingan dari koleksi.
     * @return Daftar PostingDto.
     */
    suspend fun getAllPostings(): List<PostingDto> {
        val snapshot = postingCol.get().await()
        return snapshot.documents.mapNotNull { doc ->
            // Menambahkan ID dokumen Firestore ke dalam objek DTO
            val posting = doc.toObject(PostingDto::class.java)
            posting?.copy(id = doc.id)
        }
    }

    // Di dalam kelas PostingService
    suspend fun getPostingsByUserId(userId: String): List<PostingDto> {
        val snapshot = postingCol
            .whereEqualTo("userOwnerId", userId) // <-- Filter utama ada di sini
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            val posting = doc.toObject(PostingDto::class.java)
            posting?.copy(id = doc.id)
        }
    }

    /**
     * Mengambil satu postingan spesifik berdasarkan ID-nya.
     * Fungsi ini dibutuhkan oleh PostingRepositoryImpl.getPostingById.
     * @param postingId ID dari dokumen postingan.
     * @return PostingDto jika ditemukan, atau null.
     */
    suspend fun getPostingById(postingId: String): PostingDto? {
        val document = postingCol.document(postingId).get().await()
        return if (document.exists()) {
            val posting = document.toObject(PostingDto::class.java)
            // Pastikan ID dari dokumen juga disalin ke objek DTO
            posting?.copy(id = document.id)
        } else {
            null
        }
    }

    /**
     * Mengupload postingan baru atau mengupdate yang sudah ada.
     * ID dokumen akan diambil dari properti id pada objek PostingDto.
     * @param posting Objek PostingDto yang akan di-upload.
     */
    suspend fun uploadPosting(posting: PostingDto) {
        postingCol.document(posting.id).set(posting).await()
    }

    /**
     * Menghapus postingan berdasarkan ID-nya.
     * @param postingId ID dari postingan yang akan dihapus.
     */
    suspend fun deletePosting(postingId: String) {
        postingCol.document(postingId).delete().await()
    }

    /**
     * Menambah komentar ke sebuah postingan menggunakan FieldValue.arrayUnion.
     * Ini memastikan tidak ada duplikasi komentar yang sama persis.
     * @param postId ID dari postingan yang akan dikomentari.
     * @param comment Objek CommentDto yang akan ditambahkan.
     */
    suspend fun addComment(postId: String, comment: CommentDto) {
        postingCol.document(postId)
            .update("comments", FieldValue.arrayUnion(comment))
            .await()
    }

    /**
     * Menghapus komentar dari sebuah postingan menggunakan FieldValue.arrayRemove.
     * Ini akan menghapus semua instance komentar yang cocok dengan objek yang diberikan.
     * @param postId ID dari postingan.
     * @param comment Objek CommentDto yang akan dihapus.
     */
    suspend fun deleteComment(postId: String, comment: CommentDto) {
        postingCol.document(postId)
            .update("comments", FieldValue.arrayRemove(comment))
            .await()
    }

    /**
     * Memeriksa apakah seorang pengguna sudah menyukai postingan tertentu.
     * @param postId ID postingan.
     * @param userId ID pengguna yang akan diperiksa.
     * @return Boolean `true` jika sudah di-like, `false` jika belum.
     */
    suspend fun isPostLikedByUser(postId: String, userId: String): Boolean {
        val likeDoc = postingCol.document(postId)
            .collection("likes") // Akses sub-koleksi 'likes'
            .document(userId)    // Dokumen 'like' dinamai dengan ID pengguna
            .get()
            .await()
        return likeDoc.exists()
    }

    /**
     * Menambahkan 'like' dari pengguna ke postingan dan menaikkan jumlah 'likesCount'.
     * Dilakukan secara atomik menggunakan batch write.
     * @param postId ID postingan yang akan di-like.
     * @param userId ID pengguna yang me-like.
     */
    suspend fun likePost(postId: String, userId: String) {
        val postRef = postingCol.document(postId)
        val likeRef = postRef.collection("likes").document(userId)

        // Gunakan batch write untuk memastikan kedua operasi berhasil atau gagal bersamaan
        db.runBatch { batch ->
            // 1. Tambah dokumen di sub-koleksi 'likes'
            batch.set(likeRef, mapOf("likedAt" to FieldValue.serverTimestamp()))

            // 2. Naikkan (increment) counter 'likesCount' di dokumen postingan utama
            batch.update(postRef, "likesCount", FieldValue.increment(1))
        }.await()
    }

    /**
     * Menghapus 'like' dari pengguna pada postingan dan menurunkan jumlah 'likesCount'.
     * Dilakukan secara atomik menggunakan batch write.
     * @param postId ID postingan yang akan di-unlike.
     * @param userId ID pengguna yang melakukan unlike.
     */
    suspend fun unlikePost(postId: String, userId: String) {
        val postRef = postingCol.document(postId)
        val likeRef = postRef.collection("likes").document(userId)

        // Gunakan batch write untuk konsistensi data
        db.runBatch { batch ->
            // 1. Hapus dokumen dari sub-koleksi 'likes'
            batch.delete(likeRef)

            // 2. Turunkan (decrement) counter 'likesCount' di dokumen postingan utama
            batch.update(postRef, "likesCount", FieldValue.increment(-1))
        }.await()
    }
}