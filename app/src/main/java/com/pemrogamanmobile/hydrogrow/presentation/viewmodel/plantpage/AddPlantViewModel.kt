package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage

import android.net.Uri
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
import com.pemrogamanmobile.hydrogrow.domain.usecase.garden.GardenUseCase

@HiltViewModel
class AddPlantViewModel @Inject constructor(
    private val plantUseCase: PlantUseCase,
    private val gardenUseCase: GardenUseCase
) : ViewModel() {

    var uiState by mutableStateOf(AddPlantUiState())
        private set

    init {
        getAllGardens()
    }

    private fun getAllGardens() {
        viewModelScope.launch {
            gardenUseCase.getAllGardens().collect { gardens ->
                uiState = uiState.copy(
                    availableGardens = gardens,
                    selectedGardenId = uiState.selectedGardenId ?: gardens.firstOrNull()?.id
                )
            }
        }
    }

    // ✅ DITAMBAHKAN: Fungsi untuk menangani URI gambar yang dipilih dari UI
    fun onImageSelected(uri: Uri) {
        uiState = uiState.copy(imageUri = uri)
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
        val imageUri = uiState.imageUri

        // ✅ DIPERBARUI: Validasi sekarang juga mencakup pemilihan foto
        if (uiState.plantName.isBlank() || uiState.harvestTime.isBlank() || selectedGardenId.isNullOrBlank() || uiState.cupAmount.isBlank()) {
            onError("Semua field wajib diisi.")
            return
        }
        if (imageUri == null) {
            onError("Harap pilih foto tanaman.")
            return
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)

                // 1. Unggah gambar dan dapatkan URL-nya
                val imageUrl = plantUseCase.uploadPlantImage(imageUri)

                // 2. Buat objek Plant dengan imageUrl yang didapat
                val newPlant = Plant(
                    id = UUID.randomUUID().toString(),
                    plantName = uiState.plantName,
                    harvestTime = uiState.harvestTime,
                    gardenOwnerId = selectedGardenId,
                    userOwnerId = "",
                    imageUrl = imageUrl, // ✅ Gunakan URL dari hasil unggah
                    plantingTime = System.currentTimeMillis(),
                    cupAmount = uiState.cupAmount.toIntOrNull() ?: 0
                )

                // 3. Simpan data tanaman ke database
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