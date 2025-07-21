package com.pemrogamanmobile.hydrogrow.domain.usecase.posting

import com.pemrogamanmobile.hydrogrow.domain.repository.PostingRepository
import javax.inject.Inject

class LikePostingUseCase @Inject constructor(
    private val repository: PostingRepository
) {
    /**
     * Menjalankan aksi untuk menyukai atau batal menyukai sebuah postingan.
     * @param postId ID dari postingan yang akan di-proses.
     */
    suspend operator fun invoke(postId: String) {
        repository.likeUnlikePosting(postId)
    }
}