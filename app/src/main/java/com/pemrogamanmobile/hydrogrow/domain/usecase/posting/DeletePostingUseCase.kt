package com.pemrogamanmobile.hydrogrow.domain.usecase.posting

import com.pemrogamanmobile.hydrogrow.domain.model.Posting
import com.pemrogamanmobile.hydrogrow.domain.repository.PostingRepository
import javax.inject.Inject

/**
 * Use case untuk menghapus satu postingan.
 */
class DeletePostingUseCase @Inject constructor(
    private val repository: PostingRepository
) {
    suspend operator fun invoke(posting: Posting) {
        repository.deletePosting(posting)
    }
}