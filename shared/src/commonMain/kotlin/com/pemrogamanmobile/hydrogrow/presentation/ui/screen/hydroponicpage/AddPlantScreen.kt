package com.pemrogamanmobile.hydrogrow.hydroponicpage

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pemrogamanmobile.hydrogrow.data.DataSource
import com.pemrogamanmobile.hydrogrow.data.Plant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dataSource = remember { DataSource.getInstance(context) }

    val kebunList = dataSource.hydroponicStateList
    var selectedKebunIndex by remember { mutableStateOf(0) }

    var plantName by remember { mutableStateOf("") }
    var nutrients by remember { mutableStateOf("") }
    var harvestTime by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                value = if (kebunList.isNotEmpty()) kebunList[selectedKebunIndex].gardenName else "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                kebunList.forEachIndexed { index, kebun ->
                    DropdownMenuItem(
                        text = { Text(kebun.gardenName) },
                        onClick = {
                            selectedKebunIndex = index
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = plantName,
            onValueChange = { plantName = it },
            label = { Text("Jenis Tanaman") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nutrients,
            onValueChange = { nutrients = it },
            label = { Text("Nutrisi yang Dipakai") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = harvestTime,
            onValueChange = { harvestTime = it },
            label = { Text("Masa Panen") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (kebunList.isNotEmpty()) {
                    val selectedGarden = kebunList[selectedKebunIndex]
                    val newPlant = Plant(
                        plantName = plantName,
                        idgarden = selectedGarden.idgarden,
                        nutrientsUsed = nutrients,
                        harvestTime = harvestTime
                    )
                    dataSource.addPlantToGarden(selectedGarden.idgarden, newPlant)
                }
                onBack()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Simpan")
        }
    }
}

