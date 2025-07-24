package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage.makegarden.withnoai

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.domain.usecase.garden.GardenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// UI State untuk merepresentasikan kondisi layar
data class MakeGardenUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MakeGardenViewModel @Inject constructor(
    private val gardenUseCase: GardenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MakeGardenUiState())
    val uiState: StateFlow<MakeGardenUiState> = _uiState.asStateFlow()

    fun createGarden(
        gardenName: String,
        panjang: String,
        lebar: String,
        hydroponicType: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            // Validasi input dasar
            if (gardenName.isBlank() || panjang.isBlank() || lebar.isBlank() || hydroponicType.isBlank()) {
                _uiState.update { it.copy(error = "Semua field harus diisi") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // 1. Upload gambar jika ada, dan dapatkan URL-nya
                val imageUrl = if (imageUri != null) {
                    gardenUseCase.uploadGardenImage(imageUri)
                } else {
                    // URL gambar default jika tidak ada gambar yang dipilih
                    "https://firebasestorage.googleapis.com/v0/b/hydrogrow-33a95.appspot.com/o/garden_images%2Fplaceholder.png?alt=media&token=42d13a77-94d7-4648-a9c1-4a1827581e2b"
                }

                // 2. Dapatkan ID pengguna yang sedang login
                val userId = Firebase.auth.currentUser?.uid
                if (userId == null) {
                    _uiState.update { it.copy(isLoading = false, error = "User tidak terautentikasi") }
                    return@launch
                }

                // 3. Buat objek Garden baru
                val newGarden = Garden(
                    id = UUID.randomUUID().toString(),
                    gardenName = gardenName,
                    gardenSize = (panjang.toDoubleOrNull() ?: 0.0) * (lebar.toDoubleOrNull() ?: 0.0),
                    hydroponicType = hydroponicType,
                    userOwnerId = userId,
                    imageUrl = imageUrl
                )

                // 4. Simpan ke repository
                gardenUseCase.insertGarden(newGarden)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Terjadi kesalahan") }
            }
        }
    }

    // Fungsi untuk mereset state setelah error atau sukses ditangani oleh UI
    fun resetState() {
        _uiState.update { MakeGardenUiState() }
    }
}