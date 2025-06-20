package com.pemrogamanmobile.hydrogrow.hydroponicpage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.pemrogamanmobile.hydrogrow.data.HydroponicGarden

@Composable
fun MakeHydroponic2(
    result: HydroponicResult,
    onSave: (HydroponicGarden) -> Unit,
    onBackToHome: () -> Unit
) {
    var namaKebun by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Detail Kebun Hidroponik",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Jenis Hidroponik:", style = MaterialTheme.typography.bodyLarge)
        Text(text = result.hydroponictype, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Estimasi Biaya:", style = MaterialTheme.typography.bodyLarge)
        Text(text = result.costestimation, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Nama Kebun:", style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = namaKebun,
            onValueChange = { namaKebun = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val garden = HydroponicGarden(
                    idgarden = 0,
                    gardenName = namaKebun.text,
                    gardenSize = result.gardensize,
                    plants = emptyList(),
                    hydroponicType = result.hydroponictype
                )
                onSave(garden)
                onBackToHome()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Simpan Kebun")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBackToHome,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Kembali ke Home")
        }
    }
}