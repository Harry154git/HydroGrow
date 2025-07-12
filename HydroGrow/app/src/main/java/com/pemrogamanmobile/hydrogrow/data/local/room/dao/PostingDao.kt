package com.pemrogamanmobile.hydrogrow.data.local.room.dao

import androidx.room.*
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostingDao {

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