package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pemrogamanmobile.hydrogrow.R
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage.EditPlantViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    viewModel: EditPlantViewModel = hiltViewModel(),
    navController: NavController,
    plantId: String
) {
    val uiState by viewModel::uiState
    val context = LocalContext.current

    // ✅ DITAMBAHKAN: Launcher untuk memilih gambar
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.onImageSelected(it) }
        }
    )

    LaunchedEffect(plantId) {
        viewModel.loadPlant(plantId)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Operasi berhasil", Toast.LENGTH_SHORT).show()
            navController.popBackStack(navController.graph.startDestinationId, false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Tanaman",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.originalPlant == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val plantName = uiState.plantName
        val harvestTime = uiState.harvestTime
        val cupAmount = uiState.cupAmount
        val plantingTime = uiState.originalPlant?.plantingTime

        val formattedPlantingTime = plantingTime?.let {
            SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date(it))
        } ?: "Memuat..."

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ✅ DIPERBARUI: Tampilkan gambar baru atau gambar lama
            AsyncImage(
                model = uiState.newImageUri ?: uiState.originalPlant?.imageUrl,
                contentDescription = "Foto Tanaman",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { imagePickerLauncher.launch("image/*") }, // Buat gambar bisa diklik
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_notification_logo)
            )

            // ✅ DIPERBARUI: Tombol untuk meluncurkan pemilih gambar
            TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Ubah Foto")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input fields tetap sama
            EditTextField(
                label = "Nama Tanaman",
                value = plantName,
                onValueChange = { viewModel.onPlantNameChange(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            EditTextField(
                label = "Waktu Menanam",
                value = formattedPlantingTime,
                onValueChange = {},
                readOnly = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            EditTextField(
                label = "Waktu Masa Panen (hari)",
                value = harvestTime,
                onValueChange = { viewModel.onHarvestTimeChange(it) },
                keyboardType = KeyboardType.Number
            )
            Spacer(modifier = Modifier.height(16.dp))

            EditTextField(
                label = "Jumlah Cup",
                value = cupAmount,
                onValueChange = { viewModel.onCupAmountChange(it) },
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Aksi tetap sama
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0C0C0))
                ) {
                    Text("Batal", color = Color.Black)
                }
                Button(
                    onClick = { viewModel.updatePlant() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))
                ) {
                    Text("Simpan", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.deletePlant() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))
            ) {
                Text("Hapus Tanaman", color = Color.White)
            }

            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun EditTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE6F5E9),
                unfocusedContainerColor = Color(0xFFE6F5E9),
                disabledContainerColor = Color(0xFFF0F0F0),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}