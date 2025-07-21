package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.loginorregisterpage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.auth.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var registerSuccess by mutableStateOf(false)

    fun onEmailChange(newEmail: String) { email = newEmail }
    fun onPasswordChange(newPassword: String) { password = newPassword }
    fun onConfirmPasswordChange(newConfirm: String) { confirmPassword = newConfirm }

    fun validateInputs(): Boolean {
        if (!email.contains("@") || !email.endsWith(".com")) {
            errorMessage = "Email tidak valid"
            return false
        }

        if (password != confirmPassword) {
            errorMessage = "Password dan konfirmasi tidak cocok"
            return false
        }

        return true
    }

    fun register() {
        if (!validateInputs()) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val result = signInWithGoogleUseCase.register(email, password)
            result.onSuccess {
                registerSuccess = true
            }.onFailure {
                errorMessage = it.message
            }

            isLoading = false
        }
    }
}