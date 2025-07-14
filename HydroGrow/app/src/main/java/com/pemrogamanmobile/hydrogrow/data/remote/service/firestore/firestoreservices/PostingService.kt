package com.pemrogamanmobile.hydrogrow.data.remote.service.firestore.firestoreservices

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pemrogamanmobile.hydrogrow.data.remote.dto.CommentDto
import com.pemrogamanmobile.hydrogrow.data.remote.dto.PostingDto
import kotlinx.coroutines.tasks.await

class PostingService(private val db: FirebaseFirestore) {

    private val postingCol = db.collection("postings")

    // Mengambil semua postingan
    suspend fun getPostings(): List<PostingDto> {
        val snapshot = postingCol.get().await()
        return snapshot.documents.mapNotNull { it.toObject(PostingDto::class.java) }
    }

    // Mengupload postingan baru atau mengupdate yang sudah ada
    suspend fun uploadPosting(posting: PostingDto) {
        postingCol.document(posting.id).set(posting).await()
    }

    // Menghapus postingan
    suspend fun deletePosting(postingId: String) {
        postingCol.document(postingId).delete().await()
    }

    // Menambah komentar ke sebuah postingan
    suspend fun addComment(postId: String, comment: CommentDto) {
        postingCol.document(postId)
            .update("comments", FieldValue.arrayUnion(comment))
            .await()
    }

    // Menghapus komentar dari sebuah postingan
    suspend fun deleteComment(postId: String, comment: CommentDto) {
        postingCol.document(postId)
            .update("comments", FieldValue.arrayRemove(comment))
            .await()
    }
}