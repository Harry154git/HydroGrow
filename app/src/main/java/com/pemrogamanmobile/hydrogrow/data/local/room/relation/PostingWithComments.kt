package com.pemrogamanmobile.hydrogrow.data.local.room.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.CommentEntity
import com.pemrogamanmobile.hydrogrow.data.local.room.entity.PostingEntity

data class PostingWithComments(
    @Embedded
    val posting: PostingEntity,

    @Relation(
        parentColumn = "id", // Primary key dari PostingEntity
        entityColumn = "postId"  // Foreign key dari CommentEntity
    )
    val comments: List<CommentEntity>
)