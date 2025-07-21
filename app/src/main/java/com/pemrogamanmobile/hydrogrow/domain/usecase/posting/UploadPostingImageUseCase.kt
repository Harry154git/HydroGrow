package com.pemrogamanmobile.hydrogrow.domain.usecase.posting

import android.net.Uri
import com.pemrogamanmobile.hydrogrow.domain.repository.PostingRepository
import javax.inject.Inject

/**
 * Use case untuk mengunggah gambar postingan.
 */
class UploadPostingImageUseCase @Inject constructor(
    private val repository: PostingRepository
) {
    suspend operator fun invoke(uri: Uri): String {
        return repository.uploadPostingImage(uri)
    }
}