package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage.makegarden.withai.MakeGardenViewModel

@Composable
fun MakeGardenInput(
    viewModel: MakeGardenViewModel = hiltViewModel(),
    onNext: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text("Buat Kebun Hidroponik Baru", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        InputField("Pencahayaan (jam per hari)", viewModel.pencahayaan) { viewModel.pencahayaan = it }
        InputField("Intensitas Cahaya (lux)", viewModel.intensitasCahaya) { viewModel.intensitasCahaya = it }

        Text("Luas Lahan", style = MaterialTheme.typography.titleLarge)
        InputField("Panjang (m)", viewModel.panjang) { viewModel.panjang = it }
        InputField("Lebar (m)", viewModel.lebar) { viewModel.lebar = it }

        InputField("Lokasi Kebun (kota/desa)", viewModel.kondisiLingkungan) { viewModel.kondisiLingkungan = it }
        InputField("Suhu rata-rata (°C)", viewModel.suhu) { viewModel.suhu = it }
        InputField("Kelembaban rata-rata (%)", viewModel.kelembaban) { viewModel.kelembaban = it }

        InputField("Jenis Tanaman yang ingin ditanam", viewModel.jenisTanaman) { viewModel.jenisTanaman = it }
        InputField("Target Produksi (kg/bulan)", viewModel.targetProduksi) { viewModel.targetProduksi = it }
        InputField("Budget (Rp)", viewModel.budget) { viewModel.budget = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.analyzeGarden()
                onNext()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Lanjut")
        }
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Text(text = "$label:", style = MaterialTheme.typography.bodyLarge)
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
}
