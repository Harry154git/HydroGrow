package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.profilpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.auth.SignOutUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.preferences.GetCachedUserUseCase
import com.pemrogamanmobile.hydrogrow.presentation.uistate.UserUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCachedUserUseCase: GetCachedUserUseCase,
    private val signOutUseCase: SignOutUseCase
    // ✅ UpdateUserUseCase DIHAPUS karena tidak ada fitur update
) : ViewModel() {

    private val _state = MutableStateFlow(UserUiState())
    val state: StateFlow<UserUiState> = _state

    init {
        getProfile()
    }

    private fun getProfile() {
        viewModelScope.launch {
            getCachedUserUseCase()
                .onStart { _state.value = _state.value.copy(isLoading = true) }
                .catch { e ->
                    _state.value = _state.value.copy(error = e.message, isLoading = false)
                }
                .collect { user ->
                    _state.value = _state.value.copy(
                        user = user,
                        isLoading = false
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }

    // ✅ FUNGSI updateProfile() DAN resetSuccessState() DIHAPUS
}