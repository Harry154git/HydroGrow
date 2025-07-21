package com.pemrogamanmobile.hydrogrow.domain.usecase.posting

import com.pemrogamanmobile.hydrogrow.domain.model.Comment
import com.pemrogamanmobile.hydrogrow.domain.repository.PostingRepository
import javax.inject.Inject

/**
 * Use case untuk menambahkan komentar baru.
 */
class AddCommentUseCase @Inject constructor(
    private val repository: PostingRepository
) {
    suspend operator fun invoke(comment: Comment) {
        repository.addComment(comment)
    }
}