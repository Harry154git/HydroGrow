package com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage.makegarden.withai

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import com.pemrogamanmobile.hydrogrow.domain.usecase.ai.creategarden.CreateGardenUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.auth.GetCurrentUserUseCase
import com.pemrogamanmobile.hydrogrow.domain.usecase.garden.GardenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MakeGardenViewModel @Inject constructor(
    private val createGardenUseCase: CreateGardenUseCase,
    private val gardenUseCase: GardenUseCase,
    private val authUseCase: GetCurrentUserUseCase
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
        // nanti ya
    }

    //private fun getCurrentUserId(): String? {
        // nanti ya
    //}

    fun saveGarden(gardenName: String, hydroponicType: String, gardenSize: Double) {
        // nanti ya
    }
}
