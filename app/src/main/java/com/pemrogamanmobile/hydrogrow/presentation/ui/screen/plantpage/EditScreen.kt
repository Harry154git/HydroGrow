package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pemrogamanmobile.hydrogrow.R
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage.EditPlantViewModel

@Composable
fun EditScreen(
    viewModel: EditPlantViewModel = hiltViewModel(),
    navController: NavController,
    plantId: String
) {
    val state by viewModel::uiState
    var isEditing by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    // Local fields untuk menampung perubahan saat mode edit
    var localPlantName by rememberSaveable { mutableStateOf("") }
    var localHarvestTime by rememberSaveable { mutableStateOf("") }
    var localCupAmount by rememberSaveable { mutableStateOf("") } // ✅ DITAMBAHKAN

    LaunchedEffect(plantId) {
        viewModel.loadPlant(plantId)
    }

    // ✅ DIPERBARUI: Inisialisasi local state saat data dari ViewModel pertama kali siap
    LaunchedEffect(state.originalPlant) {
        if (state.originalPlant != null) {
            localPlantName = state.plantName
            localHarvestTime = state.harvestTime
            localCupAmount = state.cupAmount
        }
    }

    // Navigasi kembali setelah sukses
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, "Operasi berhasil", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    if (state.isLoading && state.originalPlant == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Edit Tanaman", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Button(onClick = { navController.popBackStack() }) {
                Text("Kembali")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = state.originalPlant?.imageUrl,
            contentDescription = "Tanaman",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_notification_logo) // placeholder
        )

        Spacer(modifier = Modifier.height(24.dp))

        InfoRow("Nama Tanaman:", if (isEditing) localPlantName else state.plantName, isEditing) { localPlantName = it }
        InfoRow("Masa Panen:", if (isEditing) localHarvestTime else state.harvestTime, isEditing) { localHarvestTime = it }
        InfoRow("Jumlah Cup:", if (isEditing) localCupAmount else state.cupAmount, isEditing) { localCupAmount = it } // ✅ DITAMBAHKAN

        // ✅ DIHAPUS - InfoRow untuk nutrisi
        // InfoRow("Nutrisi:", if (isEditing) localNutrients else state.nutrientsUsed, isEditing) { localNutrients = it }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                if (!isEditing) {
                    // Saat masuk mode edit, pastikan local state sinkron dengan state terbaru
                    localPlantName = state.plantName
                    localHarvestTime = state.harvestTime
                    localCupAmount = state.cupAmount
                }
                isEditing = !isEditing
            }) {
                Text(if (isEditing) "Batal" else "Edit")
            }

            if (isEditing) {
                Button(onClick = {
                    // Update state di ViewModel dengan nilai dari local state
                    viewModel.onPlantNameChange(localPlantName)
                    viewModel.onHarvestTimeChange(localHarvestTime)
                    viewModel.onCupAmountChange(localCupAmount)
                    viewModel.updatePlant()
                    // Navigasi kembali akan ditangani oleh LaunchedEffect(state.isSuccess)
                }) {
                    Text("Simpan")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.deletePlant() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hapus Tanaman", color = Color.White)
        }

        state.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Error: $it", color = Color.Red)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.SemiBold)
        if (isEditing) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                // Sesuaikan keyboard type jika perlu
                keyboardOptions = if (label.contains("Cup")) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default
            )
        } else {
            Text(value, modifier = Modifier.padding(vertical = 8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}