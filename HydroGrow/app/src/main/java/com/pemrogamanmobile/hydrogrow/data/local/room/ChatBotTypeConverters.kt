package com.pemrogamanmobile.hydrogrow.data.local.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Mengubah List<String> menjadi String JSON untuk disimpan di Room, dan sebaliknya.
 * Jangan lupa daftarkan converter ini di kelas Database Room Anda.
 */
class ChatBotTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(conversation: List<String>?): String {
        return gson.toJson(conversation)
    }

    @TypeConverter
    fun toStringList(json: String?): MutableList<String> {
        if (json.isNullOrEmpty()) {
            return mutableListOf()
        }
        val listType = object : TypeToken<MutableList<String>>() {}.type
        return gson.fromJson(json, listType)
    }
}