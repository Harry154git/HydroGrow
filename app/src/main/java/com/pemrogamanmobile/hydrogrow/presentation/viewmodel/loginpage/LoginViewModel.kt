package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.loginpage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pemrogamanmobile.hydrogrow.domain.model.User
import com.pemrogamanmobile.hydrogrow.domain.usecase.auth.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed Interface untuk merepresentasikan UI State
sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    data class Success(val user: User) : LoginState
    data class Error(val message: String?) : LoginState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val crashlytics: FirebaseCrashlytics
) : ViewModel() {

    // State yang akan diobservasi oleh UI
    var uiState: LoginState by mutableStateOf(LoginState.Idle)
        private set

    /**
     * Fungsi untuk memulai proses sign-in dengan kredensial dari Google.
     */
    fun signInWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            uiState = LoginState.Loading // Set state menjadi Loading
            signInWithGoogleUseCase(credential)
                .onSuccess { user ->
                    // Jika berhasil, update state menjadi Success dengan data user
                    // Pengecekan user tidak null untuk keamanan
                    if (user != null) {
                        uiState = LoginState.Success(user)
                    } else {
                        uiState = LoginState.Error("Gagal mendapatkan data user.")
                    }
                }
                .onFailure { exception ->
                    // Jika gagal, update state menjadi Error dengan pesan kesalahan
                    crashlytics.log("Error : ${exception.message}")
                    uiState = LoginState.Error(exception.message)
                }
        }
    }

    /**
     * Fungsi untuk mereset state kembali ke Idle setelah navigasi atau menampilkan error.
     */
    fun resetState() {
        uiState = LoginState.Idle
    }
}