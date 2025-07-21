package com.pemrogamanmobile.hydrogrow.domain.usecase.posting

import com.pemrogamanmobile.hydrogrow.domain.model.Comment
import com.pemrogamanmobile.hydrogrow.domain.repository.PostingRepository
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val repository: PostingRepository
) {
    /**
     * Menjalankan aksi untuk menghapus sebuah komentar.
     * @param comment Objek Comment yang akan dihapus.
     */
    suspend operator fun invoke(comment: Comment) = repository.deleteComment(comment)
}