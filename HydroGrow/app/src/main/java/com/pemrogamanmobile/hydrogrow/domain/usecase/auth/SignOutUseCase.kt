package com.pemrogamanmobile.hydrogrow.domain.usecase.auth

import com.pemrogamanmobile.hydrogrow.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.signOut()
    }
}