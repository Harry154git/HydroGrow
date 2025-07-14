package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.domain.usecase.geminiai.AnalyzeHydroponicsDataUseCase
import com.pemrogamanmobile.hydrogrow.data.repository.GardenUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MakeGardenViewModel @Inject constructor(
    private val analyzeHydroponicsDataUseCase: AnalyzeHydroponicsDataUseCase,
    private val gardenUseCase: GardenUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {

    var pencahayaan by mutableStateOf("")          // Contoh: "6 jam per hari"
    var intensitasCahaya by mutableStateOf("")     // Contoh: "8000 lux"
    var panjang by mutableStateOf("")
    var lebar by mutableStateOf("")
    var kondisiLingkungan by mutableStateOf("")    // Contoh: "Bandung, suhu 24°C, kelembaban 80%"
    var suhu by mutableStateOf("")                 // Contoh: "24"
    var kelembaban by mutableStateOf("")           // Contoh: "80"
    var jenisTanaman by mutableStateOf("")         // Contoh: "Selada"
    var targetProduksi by mutableStateOf("")       // Contoh: "200"
    var budget by mutableStateOf("")               // Contoh: "30000000"

    private val _explanation = MutableStateFlow("")
    val explanation = _explanation.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun analyzeGarden() {
        viewModelScope.launch {
            _loading.value = true

            val combinedInput = """
                Pencahayaan: $pencahayaan, Intensitas: $intensitasCahaya lux
                Panjang lahan: $panjang m
                Lebar lahan: $lebar m
                Lokasi: $kondisiLingkungan, Suhu rata-rata: $suhu °C, Kelembaban: $kelembaban%
                Jenis tanaman: $jenisTanaman
                Target produksi: $targetProduksi kg/bulan
                Budget: Rp $budget
            """.trimIndent()

            val aiResponse = analyzeHydroponicsDataUseCase(combinedInput)

            _explanation.value = aiResponse
            _loading.value = false
        }
    }

    private fun getCurrentUserId(): String? {
        return userUseCase.getCurrentUserId()
    }

    fun saveGarden(gardenName: String, hydroponicType: String, gardenSize: Double) {
        viewModelScope.launch {
            _loading.value = true
            val userId = getCurrentUserId() ?: "unknown"

            val garden = Garden(
                id = java.util.UUID.randomUUID().toString(),
                gardenName = gardenName,
                gardenSize = gardenSize,
                hydroponicType = hydroponicType,
                userOwnerId = userId
            )
            gardenUseCase.insertGarden(garden)
            _loading.value = false
        }
    }
}
