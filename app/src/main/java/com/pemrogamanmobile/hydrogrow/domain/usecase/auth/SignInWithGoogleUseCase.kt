package com.pemrogamanmobile.hydrogrow.domain.usecase.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pemrogamanmobile.hydrogrow.domain.model.User
import com.pemrogamanmobile.hydrogrow.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val crashlytics: FirebaseCrashlytics
) {
    suspend operator fun invoke(credential: AuthCredential): Result<User?> {
        return try {
            val user = repository.signInWithCredential(credential)
            Result.success(user)
        } catch (e: Exception) {
            // Kita bisa menangani error spesifik di sini jika perlu
            crashlytics.log("Error : ${e.message}")
            Result.failure(e)
        }
    }
}