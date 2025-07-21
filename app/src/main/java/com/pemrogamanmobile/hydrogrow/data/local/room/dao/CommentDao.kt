package com.pemrogamanmobile.hydrogrow.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.CommentEntity

@Dao
interface CommentDao {

    /**
     * Menyisipkan satu komentar. Jika sudah ada, akan diganti.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    /**
     * Menyisipkan daftar komentar.
     * PENTING: Digunakan untuk sinkronisasi dari Firestore.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<CommentEntity>)

    /**
     * BARU: Menghapus semua komentar yang terkait dengan postId tertentu.
     * Digunakan sebelum menyisipkan daftar komentar baru saat sinkronisasi.
     */
    @Query("DELETE FROM comments WHERE postId = :postId")
    suspend fun deleteCommentsByPostId(postId: String)

    /**
     * BARU: Menghapus semua data dari tabel komentar.
     * Digunakan untuk membersihkan cache sebelum sinkronisasi total.
     */
    @Query("DELETE FROM comments")
    suspend fun deleteAllComments()
}