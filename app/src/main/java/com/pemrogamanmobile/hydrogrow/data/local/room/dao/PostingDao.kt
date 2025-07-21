package com.pemrogamanmobile.hydrogrow.data.local.room.dao

import androidx.room.*
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.relation.PostingWithComments
import kotlinx.coroutines.flow.Flow

@Dao
interface PostingDao {

    // --- OPERASI TULIS (Write Operations) ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosting(posting: PostingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostings(postings: List<PostingEntity>)

    @Update
    suspend fun updatePosting(posting: PostingEntity)

    @Delete
    suspend fun deletePosting(posting: PostingEntity)

    @Query("DELETE FROM posting WHERE id = :postingId")
    suspend fun deletePostingById(postingId: String)

    @Query("DELETE FROM posting")
    suspend fun deleteAllPostings()


    // --- OPERASI BACA (Read Operations dengan @Transaction) ---

    /**
     * DIGANTI: Mengambil semua postingan LENGKAP dengan komentarnya.
     * Menggunakan @Transaction untuk memastikan operasi ini atomic.
     * Ini menjadi "Single Source of Truth" yang sebenarnya.
     */
    @Transaction
    @Query("SELECT * FROM posting ORDER BY createdAt DESC")
    fun getPostingsWithComments(): Flow<List<PostingWithComments>>

    /**
     * DIGANTI: Mengambil satu postingan LENGKAP dengan komentarnya berdasarkan ID.
     */
    @Transaction
    @Query("SELECT * FROM posting WHERE id = :postingId LIMIT 1")
    suspend fun getPostingWithCommentsById(postingId: String): PostingWithComments?

    /**
     * DIGANTI: Mengambil semua postingan milik user tertentu, LENGKAP dengan komentarnya.
     */
    @Transaction
    @Query("SELECT * FROM posting WHERE userOwnerId = :userId ORDER BY createdAt DESC")
    fun getPostingsWithCommentsByUserId(userId: String): Flow<List<PostingWithComments>>

    @Query("SELECT * FROM posting WHERE id = :postId")
    suspend fun getPostingById(postId: String): PostingEntity?

    @Query("UPDATE posting SET likes = :newLikesCount WHERE id = :postId")
    suspend fun updateLikesCount(postId: String, newLikesCount: Int)
}