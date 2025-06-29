package com.pemrogamanmobile.hydrogrow.domain.usecase

import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.pemrogamanmobile.hydrogrow.domain.repository.UserRepository
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val repo: UserRepository
) {
    suspend fun login(email: String, password: String) = repo.login(email, password)

    suspend fun register(email: String, password: String): Result<Unit> {
        if (!password.matches(Regex("^(?=.*[a-zA-Z])(?=.*\\d).{4,}$"))) {
            return Result.failure(Exception("Password harus mengandung huruf, angka, dan minimal 4 karakter"))
        }

        return try {
            val existingUser = repo.register(email, password)
            if (existingUser != null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Registrasi gagal"))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("Email sudah digunakan"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}