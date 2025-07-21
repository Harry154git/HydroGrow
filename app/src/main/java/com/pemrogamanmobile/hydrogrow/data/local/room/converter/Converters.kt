package com.pemrogamanmobile.hydrogrow.data.local.room.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.ChatMessageEntity

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromChatMessageList(value: List<ChatMessageEntity>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toChatMessageList(value: String): List<ChatMessageEntity> {
        val listType = object : TypeToken<List<ChatMessageEntity>>() {}.type
        return gson.fromJson(value, listType)
    }
}