package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.settingspage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.preferences.PreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: PreferencesUseCase
) : ViewModel() {
    val darkMode: Flow<Boolean> = repo.observeDarkMode()
    val language: Flow<String> = repo.observeLanguage()

    fun toggleDarkMode(enabled: Boolean) = viewModelScope.launch {
        repo.setDarkMode(enabled)
    }

    fun setLanguage(lang: String) = viewModelScope.launch {
        repo.setLanguage(lang)
    }
}
