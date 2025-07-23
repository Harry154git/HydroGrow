package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.usecase.plant.PlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.pemrogamanmobile.hydrogrow.presentation.uistate.AddPlantUiState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.pemrogamanmobile.hydrogrow.domain.model.Plant
import kotlinx.coroutines.launch
import java.util.UUID
import com.pemrogamanmobile.hydrogrow.domain.usecase.auth.GetCurrentUserUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.garden.GardenUseCase

@HiltViewModel
class AddPlantViewModel @Inject constructor(
    private val plantUseCase: PlantUseCase,
    private val gardenUseCase: GardenUseCase,
    // userUseCase tetap di-inject jika diperlukan untuk fitur lain,
    // namun tidak dipakai lagi di fungsi yang Anda sebutkan.
    private val userUseCase: GetCurrentUserUseCase
) : ViewModel() {

    var uiState by mutableStateOf(AddPlantUiState())
        private set

    init {
        getAllGardens()
    }

    private fun getAllGardens() {
        viewModelScope.launch {
            // ✅ DIPERBARUI: Tidak perlu get userId manual.
            // Langsung panggil getAllGardens() dari use case.
            gardenUseCase.getAllGardens().collect { gardens ->
                uiState = uiState.copy(
                    availableGardens = gardens,
                    // Set garden pertama sebagai default jika belum ada yang terpilih
                    selectedGardenId = uiState.selectedGardenId ?: gardens.firstOrNull()?.id
                )
            }
        }
    }

    fun onPlantNameChange(name: String) {
        uiState = uiState.copy(plantName = name)
    }

    fun onCupAmountChange(amount: String) {
        if (amount.all { it.isDigit() }) {
            uiState = uiState.copy(cupAmount = amount)
        }
    }

    fun onHarvestTimeChange(time: String) {
        uiState = uiState.copy(harvestTime = time)
    }

    fun onGardenSelected(gardenId: String) {
        uiState = uiState.copy(selectedGardenId = gardenId)
    }

    fun savePlant(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val selectedGardenId = uiState.selectedGardenId
        if (uiState.plantName.isBlank() || uiState.harvestTime.isBlank() || selectedGardenId.isNullOrBlank() || uiState.cupAmount.isBlank()) {
            onError("Semua field wajib diisi.")
            return
        }

        viewModelScope.launch {
            // ✅ DIPERBARUI: Tidak perlu get userId manual.

            val newPlant = Plant(
                id = UUID.randomUUID().toString(),
                plantName = uiState.plantName,
                harvestTime = uiState.harvestTime,
                gardenOwnerId = selectedGardenId,
                userOwnerId = "", // Dikosongkan, karena akan diisi oleh implementasi insertPlant
                imageUrl = null,
                plantingTime = System.currentTimeMillis(),
                cupAmount = uiState.cupAmount.toIntOrNull() ?: 0
            )

            try {
                uiState = uiState.copy(isLoading = true)
                // ✅ DIPERBARUI: Panggil insertPlant tanpa perlu mengambil userId di sini.
                plantUseCase.insertPlant(newPlant)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Gagal menyimpan tanaman")
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }
}