package com.pemrogamanmobile.hydrogrow.data.local.room.dao

import androidx.room.*
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.ChatBotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatBotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatBot(chatbot: ChatBotEntity)

    @Update
    suspend fun updateChatBot(chatbot: ChatBotEntity)

    @Delete
    suspend fun deleteChatBot(chatbot: ChatBotEntity)

    @Query("SELECT * FROM chatbot WHERE id = :chatbotId LIMIT 1")
    suspend fun getChatBotById(chatbotId: String): ChatBotEntity?

    @Query("SELECT * FROM chatbot WHERE userOwnerId = :userId")
    fun getChatBotByUserId(userId: String): Flow<List<ChatBotEntity>>

    @Query("DELETE FROM chatbot")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chatbot: List<ChatBotEntity>)

    @Transaction
    suspend fun replaceAll(chatbot: List<ChatBotEntity>) {
        deleteAll()
        insertAll(chatbot)
    }
}