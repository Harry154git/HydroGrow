package com.pemrogamanmobile.hydrogrow.data.local.room.dao

import androidx.room.*
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostingDao {

    /**
     * Menyisipkan satu postingan. Jika sudah ada, akan diganti.
     * Digunakan oleh: `insertPosting`, `updatePosting` (saat get dari remote).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosting(posting: PostingEntity)

    /**
     * Menyisipkan daftar postingan. Jika ada yang konflik, akan diganti.
     * Digunakan oleh: `createSyncedPostingFlow` untuk menyimpan data dari Firestore.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostings(postings: List<PostingEntity>)

    /**
     * Memperbarui postingan yang sudah ada.
     * Digunakan oleh: `updatePosting`, `addComment`, `deleteComment`.
     */
    @Update
    suspend fun updatePosting(posting: PostingEntity)

    /**
     * Menghapus postingan berdasarkan objek entity-nya.
     * Digunakan oleh: `deletePosting`.
     */
    @Delete
    suspend fun deletePosting(posting: PostingEntity)

    /**
     * Menghapus postingan berdasarkan ID-nya.
     * Digunakan oleh: `getPostingById` untuk menjaga konsistensi data.
     */
    @Query("DELETE FROM posting WHERE id = :postingId")
    suspend fun deletePostingById(postingId: String)

    /**
     * Menghapus semua postingan dari tabel.
     * Digunakan oleh: `createSyncedPostingFlow` sebelum memasukkan data baru.
     */
    @Query("DELETE FROM posting")
    suspend fun deleteAllPostings()

    // Di dalam interface PostingDao (sudah ada)
    @Query("SELECT * FROM posting WHERE userOwnerId = :userId")
    fun getPostingByUserId(userId: String): Flow<List<PostingEntity>>

    /**
     * Mengambil satu postingan berdasarkan ID-nya.
     * Digunakan oleh: `getPostingById`, `addComment`, `deleteComment`.
     * @return PostingEntity jika ditemukan, atau null.
     */
    @Query("SELECT * FROM posting WHERE id = :postingId LIMIT 1")
    suspend fun getPostingById(postingId: String): PostingEntity?

    /**
     * Mengambil semua postingan sebagai Flow, diurutkan dari yang terbaru.
     * Menjadi "Single Source of Truth" untuk UI.
     * Digunakan oleh: `getAllPostings`.
     * @return Flow yang berisi daftar PostingEntity.
     */
    @Query("SELECT * FROM posting ORDER BY createdAt DESC")
    fun getAllPostings(): Flow<List<PostingEntity>>
}