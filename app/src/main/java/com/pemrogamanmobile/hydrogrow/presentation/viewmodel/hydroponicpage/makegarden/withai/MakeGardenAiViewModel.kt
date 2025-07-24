package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage.makegarden.withai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.model.AiGardenPlan
import com.pemrogamanmobile.hydrogrow.domain.model.GardenCreationParams
import com.pemrogamanmobile.hydrogrow.domain.usecase.ai.creategarden.CreateGardenUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.auth.GetCurrentUserUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.preferences.GetCachedUserUseCase
import com.pemrogamanmobile.hydrogrow.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State untuk menampung seluruh data dalam alur pembuatan kebun dengan AI.
 */
data class MakeGardenAiUiState(
    // Navigasi & UI
    val currentPage: Int = 0, // 0: Intro, 1-6: Pertanyaan, 7: Hasil
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaveSuccess: Boolean = false,
    val userName: String = "",

    // --- State untuk semua jawaban kuesioner ---
    // Pertanyaan 1: Kondisi Cahaya
    val kondisiCahaya: String = "",
    // Pertanyaan 2: Suhu Cuaca
    val suhuCuaca: String = "",
    // Pertanyaan 3: Tujuan/Skala
    val tujuanSkala: String = "",
    // Pertanyaan 4: Perkiraan Biaya
    val rentangBiaya: String = "",
    // Pertanyaan 5: Ukuran Lahan
    val mintaRekomendasiLahan: Boolean = true,
    val panjangLahan: String = "",
    val lebarLahan: String = "",
    // Pertanyaan 6: Jenis Tanaman
    val mintaRekomendasiTanaman: Boolean = true,
    val jenisTanaman: String = "",

    // State untuk hasil dari AI
    val aiPlan: AiGardenPlan? = null,
    val gardenName: String = "" // Nama kebun yang diinput pengguna di halaman hasil
)

@HiltViewModel
class MakeGardenAiViewModel @Inject constructor(
    private val createGardenUseCase: CreateGardenUseCase,
    private val authUseCase: GetCurrentUserUseCase,
    private val getCachedUserUseCase: GetCachedUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MakeGardenAiUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserName()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            getCachedUserUseCase().collect { user ->
                _uiState.update { it.copy(userName = user?.name ?: "Sobat Hydro") }
            }
        }
    }

    // --- Fungsi Navigasi Halaman ---
    fun nextPage() {
        if (_uiState.value.isLoading) return
        val currentPage = _uiState.value.currentPage
        if (currentPage == 6) { // Jika di halaman terakhir pertanyaan, panggil AI
            getAIRecommendation()
        } else if (currentPage < 7) {
            _uiState.update { it.copy(currentPage = currentPage + 1) }
        }
    }

    fun previousPage() {
        if (_uiState.value.isLoading) return
        val currentPage = _uiState.value.currentPage
        if (currentPage > 0) {
            _uiState.update { it.copy(currentPage = currentPage - 1) }
        }
    }

    // --- Fungsi Pengelolaan State ---
    fun onStateChange(newState: MakeGardenAiUiState) {
        _uiState.value = newState
    }

    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }

    // --- Fungsi Logika Inti ---
    private fun getAIRecommendation() {
        val params = GardenCreationParams(
            kondisiCahaya = _uiState.value.kondisiCahaya,
            suhuCuaca = _uiState.value.suhuCuaca,
            tujuanSkala = _uiState.value.tujuanSkala,
            rentangBiaya = _uiState.value.rentangBiaya,
            mintaRekomendasiLahan = _uiState.value.mintaRekomendasiLahan,
            panjangLahan = _uiState.value.panjangLahan.toIntOrNull(),
            lebarLahan = _uiState.value.lebarLahan.toIntOrNull(),
            mintaRekomendasiTanaman = _uiState.value.mintaRekomendasiTanaman,
            jenisTanaman = _uiState.value.jenisTanaman
        )

        createGardenUseCase.createNewGardenWithAi(params).onEach { result ->
            when (result) {
                is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            aiPlan = result.data,
                            // Set nama kebun awal dari AI, bisa diubah pengguna
                            gardenName = result.data?.let { plan -> "Kebun ${plan.hydroponicType}" } ?: "Kebun Baru",
                            currentPage = 7 // Pindah ke halaman hasil
                        )
                    }
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun saveGardenPlan() {
        val plan = _uiState.value.aiPlan
        val gardenName = _uiState.value.gardenName
        val currentUser = authUseCase.invoke()

        if (plan == null) {
            _uiState.update { it.copy(error = "Rencana AI tidak ditemukan.") }
            return
        }
        if (gardenName.isBlank()) {
            _uiState.update { it.copy(error = "Nama kebun tidak boleh kosong.") }
            return
        }
        if (currentUser == null) {
            _uiState.update { it.copy(error = "Gagal mendapatkan data pengguna.") }
            return
        }

        createGardenUseCase.saveGardenAndChat(
            userOwnerId = currentUser.uid,
            gardenName = gardenName,
            plan = plan
        ).onEach { result ->
            when (result) {
                is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isSaveSuccess = true)
                    }
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }.launchIn(viewModelScope)
    }
}