package com.pemrogamanmobile.hydrogrow.hydroponicpage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun MakeHydroponic(
    onNextScreen: (HydroponicResult) -> Unit
) {
    var pencahayaan by remember { mutableStateOf("") }
    var panjang by remember { mutableStateOf("") }
    var lebar by remember { mutableStateOf("") }
    var modalPembuatan by remember { mutableStateOf("") }
    var kondisiLingkungan by remember { mutableStateOf("") }
    var jenisTanaman by remember { mutableStateOf("") }
    var nutrisi by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Buat Kebun Hidroponik Baru", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        InputField("Pencahayaan", pencahayaan) { pencahayaan = it }
        InputField("Panjang (m)", panjang) { panjang = it }
        InputField("Lebar (m)", lebar) { lebar = it }
        InputField("Modal Pembuatan (Rp)", modalPembuatan) { modalPembuatan = it }
        InputField("Kondisi Lingkungan", kondisiLingkungan) { kondisiLingkungan = it }
        InputField("Jenis Tanaman", jenisTanaman) { jenisTanaman = it }
        InputField("Nutrisi yang Dipakai", nutrisi) { nutrisi = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val panjangVal = panjang.toDoubleOrNull() ?: 0.0
                val lebarVal = lebar.toDoubleOrNull() ?: 0.0
                val luas = panjangVal * lebarVal
                val modal = modalPembuatan.toDoubleOrNull() ?: 0.0

                val jenisHidroponik = when {
                    pencahayaan.contains("terang", ignoreCase = true) &&
                            kondisiLingkungan.contains("hangat", ignoreCase = true) -> "NFT"
                    pencahayaan.contains("redup", ignoreCase = true) -> "DWC"
                    else -> "Wick System"
                }

                val estimasiBiaya = (luas * 100_000 + modal).roundToInt()

                val result = HydroponicResult(
                    hydroponictype = jenisHidroponik,
                    costestimation = "Rp $estimasiBiaya",
                    gardensize = luas
                )

                onNextScreen(result)
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
