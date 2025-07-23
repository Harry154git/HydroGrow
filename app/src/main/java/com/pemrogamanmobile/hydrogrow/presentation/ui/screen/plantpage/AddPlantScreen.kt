package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage.AddPlantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    viewModel: AddPlantViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by remember { viewModel::uiState } // Gunakan delegasi agar recomposition terjadi
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
            Text(
                text = "Tambah Tanaman",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Pilih Kebun:", modifier = Modifier.fillMaxWidth())

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = state.availableGardens.find { it.id == state.selectedGardenId }?.gardenName ?: "Pilih Kebun",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                state.availableGardens.forEach { kebun ->
                    DropdownMenuItem(
                        text = { Text(kebun.gardenName) },
                        onClick = {
                            viewModel.onGardenSelected(kebun.id)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.plantName,
            onValueChange = { viewModel.onPlantNameChange(it) },
            label = { Text("Jenis Tanaman (cth: Kangkung, Selada)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ DIHAPUS - TextField untuk Nutrisi tidak lagi diperlukan
        /*
        OutlinedTextField(
            value = state.nutrientsUsed,
            onValueChange = { viewModel.onNutrientsChange(it) },
            label = { Text("Nutrisi yang Dipakai") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.nutrientLocked
        )
        Spacer(modifier = Modifier.height(8.dp))
        */

        OutlinedTextField(
            value = state.harvestTime,
            onValueChange = { viewModel.onHarvestTimeChange(it) },
            label = { Text("Masa Panen (cth: 30 hari)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ DITAMBAHKAN - TextField untuk Jumlah Cup
        OutlinedTextField(
            value = state.cupAmount,
            onValueChange = { viewModel.onCupAmountChange(it) },
            label = { Text("Jumlah Cup") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.savePlant(
                    onSuccess = {
                        Toast.makeText(context, "Tanaman berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        onBack()
                    },
                    onError = { message ->
                        Toast.makeText(context, message ?: "Terjadi kesalahan", Toast.LENGTH_LONG).show()
                    }
                )
            },
            enabled = !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Simpan")
            }
        }
    }
}