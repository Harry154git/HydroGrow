package com.pemrogamanmobile.hydrogrow.data.local.room.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PostingWithComments(

    /**
     * Properti untuk menampung data induk (satu postingan).
     * @Embedded memberitahu Room untuk memperlakukan semua kolom dari PostingEntity
     * seolah-olah mereka adalah kolom langsung di dalam kelas ini.
     */
    @Embedded
    val posting: PostingEntity,

    /**
     * Properti untuk menampung data anak (daftar komentar).
     * @Relation mendefinisikan hubungan antara data induk dan anak.
     * - parentColumn = "id": Ini adalah kolom Primary Key di tabel induk (tabel postings).
     * - entityColumn = "postId": Ini adalah kolom Foreign Key di tabel anak (tabel comments)
     * yang merujuk ke 'id' postingan.
     */
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val comments: List<CommentEntity>
)