package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.profilpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.UserUseCase
import com.pemrogamanmobile.hydrogrow.presentation.mapper.toDomain
import com.pemrogamanmobile.hydrogrow.presentation.mapper.toUi
import com.pemrogamanmobile.hydrogrow.presentation.model.UserUi
import com.pemrogamanmobile.hydrogrow.presentation.ui.uistate.UserUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UserUiState(isLoading = true))
    val state: StateFlow<UserUiState> = _state

    init {
        getProfile()
    }

    private fun getProfile() {
        viewModelScope.launch {
            userUseCase.getProfile()
                .onEach { user ->
                    _state.value = _state.value.copy(
                        user = user?.toUi(),
                        isLoading = false,
                        error = null
                    )
                }
                .catch { e ->
                    _state.value = _state.value.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
                .collect {}
        }
    }

    fun updateProfile(userUi: UserUi) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val domainUser = userUi.toDomain(
                    id = userUseCase.getCurrentUserId() ?: ""
                )
                userUseCase.updateProfile(domainUser)
                _state.value = _state.value.copy(user = userUi, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userUseCase.logout()
            _state.value = UserUiState(user = null)
        }
    }
}