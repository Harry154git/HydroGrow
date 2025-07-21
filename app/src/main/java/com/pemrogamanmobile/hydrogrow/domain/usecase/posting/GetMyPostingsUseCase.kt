package com.pemrogamanmobile.hydrogrow.domain.usecase.posting

import com.pemrogamanmobile.hydrogrow.domain.model.Posting
import com.pemrogamanmobile.hydrogrow.domain.repository.PostingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case untuk mendapatkan postingan milik pengguna yang sedang login.
 */
class GetMyPostingsUseCase @Inject constructor(
    private val repository: PostingRepository
) {
    operator fun invoke(): Flow<List<Posting>> {
        return repository.getMyPostings()
    }
}