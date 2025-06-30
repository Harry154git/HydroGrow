package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage.AddPlantViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    viewModel: AddPlantViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state = viewModel.uiState
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
            Text(text = "Tambah Tanaman", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Pilih Kebun:")

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
                onDismissRequest = { expanded = false }
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
            label = { Text("Jenis Tanaman") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.nutrientsUsed,
            onValueChange = { viewModel.onNutrientsChange(it) },
            label = { Text("Nutrisi yang Dipakai") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.nutrientLocked
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.harvestTime,
            onValueChange = { viewModel.onHarvestTimeChange(it) },
            label = { Text("Masa Panen") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.savePlant(
                    onSuccess = { onBack() },
                    onError = { message ->
                        Toast.makeText(context, message ?: "Terjadi kesalahan", Toast.LENGTH_LONG).show()
                    }
                )
            },
            enabled = !state.isLoading,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Simpan")
        }
    }
}


