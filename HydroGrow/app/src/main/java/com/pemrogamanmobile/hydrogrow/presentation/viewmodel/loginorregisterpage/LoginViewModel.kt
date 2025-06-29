package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.loginorregisterpage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successLogin by mutableStateOf(false)

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun validate(): Boolean {
        if (!email.contains("@") || !email.endsWith(".com")) {
            errorMessage = "Email harus valid dan mengandung @ serta diakhiri dengan .com"
            return false
        }
        if (password.length < 4) {
            errorMessage = "Password minimal 4 karakter"
            return false
        }
        return true
    }

    fun login() {
        if (!validate()) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val user = authUseCase.login(email, password)
                if (user != null) {
                    successLogin = true
                } else {
                    errorMessage = "Login gagal, coba periksa kembali email dan password"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Terjadi kesalahan tidak diketahui"
            } finally {
                isLoading = false
            }
        }
    }
}
