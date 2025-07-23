package com.pemrogamanmobile.hydrogrow.domain.usecase.auth

import com.pemrogamanmobile.hydrogrow.domain.repository.AuthRepository
import javax.inject.Inject

class GetAuthStateFlowUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.getAuthStateFlow()
}