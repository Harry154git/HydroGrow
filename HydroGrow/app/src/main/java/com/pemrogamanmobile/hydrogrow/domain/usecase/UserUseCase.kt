package com.pemrogamanmobile.hydrogrow.domain.usecase

import com.pemrogamanmobile.hydrogrow.domain.model.User
import com.pemrogamanmobile.hydrogrow.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    fun getCurrentUserId(): String? {
        return repository.getCurrentUserId()
    }

    fun getProfile(): Flow<User?> {
        return repository.getProfile()
    }

    suspend fun fetchAndCacheProfile() {
        repository.fetchAndCacheProfile()
    }

    suspend fun updateProfile(user: User) {
        repository.updateProfile(user)
    }

    suspend fun logout() {
        repository.logout()
    }
}
