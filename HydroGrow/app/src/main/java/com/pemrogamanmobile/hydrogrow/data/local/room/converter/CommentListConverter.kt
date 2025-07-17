package com.pemrogamanmobile.hydrogrow.data.local.room.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pemrogamanmobile.hydrogrow.domain.model.Comment

class CommentListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromCommentList(comments: List<Comment>): String {
        return gson.toJson(comments)
    }

    @TypeConverter
    fun toCommentList(commentsString: String): List<Comment> {
        val listType = object : TypeToken<List<Comment>>() {}.type
        return gson.fromJson(commentsString, listType)
    }
}