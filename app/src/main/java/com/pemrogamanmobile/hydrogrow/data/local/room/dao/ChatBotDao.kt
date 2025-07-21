package com.pemrogamanmobile.hydrogrow.data.local.room.dao

import androidx.room.*
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.ChatBotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatBotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatBotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChats(chats: List<ChatBotEntity>)

    @Update
    suspend fun updateChat(chat: ChatBotEntity)

    @Delete
    suspend fun deleteChat(chat: ChatBotEntity)

    @Query("DELETE FROM chatbot WHERE id = :chatId")
    suspend fun deleteChatById(chatId: String)

    @Query("SELECT * FROM chatbot WHERE id = :chatId LIMIT 1")
    suspend fun getChatById(chatId: String): ChatBotEntity?

    @Query("SELECT * FROM chatbot WHERE userownerid = :userId ORDER BY updatedAt DESC")
    fun getChatsByUserId(userId: String): Flow<List<ChatBotEntity>>

    @Query("DELETE FROM chatbot WHERE userownerid = :userId")
    suspend fun deleteAllChatsByUserId(userId: String)
}