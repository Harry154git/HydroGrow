package com.pemrogamanmobile.hydrogrow.data.local.room.dao

import androidx.room.*
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingEntity
import androidx.room.Transaction
import com.pemrogamanmobile.hydrogrow.data.local.room.relation.PostingWithComments
import kotlinx.coroutines.flow.Flow

@Dao
interface PostingDao {

    @Transaction // Penting untuk memastikan operasi query berjalan konsisten
    @Query("SELECT * FROM posting WHERE id = :postId")
    fun getPostingWithComments(postId: String): Flow<PostingWithComments>

    @Transaction
    @Query("SELECT * FROM posting ORDER BY createdAt DESC")
    fun getAllPostingsWithComments(): Flow<List<PostingWithComments>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosting(posting: PostingEntity)

    @Update
    suspend fun updatePosting(posting: PostingEntity)

    @Delete
    suspend fun deletePosting(posting: PostingEntity)

    @Query("SELECT * FROM posting WHERE id = :postingId LIMIT 1")
    suspend fun getPostingById(postingId: String): PostingEntity?

    @Query("SELECT * FROM posting WHERE userOwnerId = :userId")
    fun getPostingByUserId(userId: String): Flow<List<PostingEntity>>

    @Query("DELETE FROM posting")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posting: List<PostingEntity>)

    @Transaction
    suspend fun replaceAll(posting: List<PostingEntity>) {
        deleteAll()
        insertAll(posting)
    }
}