package com.pemrogamanmobile.hydrogrow.domain.usecase.posting

import com.pemrogamanmobile.hydrogrow.domain.model.Posting
import com.pemrogamanmobile.hydrogrow.domain.repository.PostingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case untuk mendapatkan semua postingan dari semua pengguna (feed umum).
 */
class GetAllPostingsUseCase @Inject constructor(
    private val repository: PostingRepository
) {
    operator fun invoke(): Flow<List<Posting>> {
        return repository.getAllPostings()
    }
}