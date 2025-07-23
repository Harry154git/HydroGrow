package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage.makegarden.withai.MakeGardenViewModel

@Composable
fun MakeGardenOutput(
    viewModel: MakeGardenViewModel = hiltViewModel(),
    onBackToHome: () -> Unit
) {
    // Pastikan panggil analisis saat screen pertama kali muncul
    LaunchedEffect(Unit) {
        viewModel.analyzeGarden()
    }

    val explanation by viewModel.explanation.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var namaKebun by remember { mutableStateOf(TextFieldValue()) }
    var tipeKebun by remember { mutableStateOf(TextFieldValue()) }
    var luasKebun by remember { mutableStateOf(TextFieldValue()) }
    val scrollState = rememberScrollState()

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text("Penjelasan AI", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text(explanation, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))

            Text("Nama Kebun:")
            TextField(value = namaKebun, onValueChange = { namaKebun = it }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Text("Tipe Hidroponik:")
            TextField(value = tipeKebun, onValueChange = { tipeKebun = it }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Text("Luas Kebun (m²):")
            TextField(
                value = luasKebun,
                onValueChange = { luasKebun = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                val size = luasKebun.text.toDoubleOrNull() ?: 0.0
                viewModel.saveGarden(namaKebun.text, tipeKebun.text, size)
                onBackToHome()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Simpan Kebun")
            }
        }
    }
}
