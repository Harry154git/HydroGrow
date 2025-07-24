package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pemrogamanmobile.hydrogrow.presentation.uistate.PlantUiState
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage.PlantViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantScreen(
    navController: NavController,
    plantId: String,
    viewModel: PlantViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(plantId) {
        viewModel.loadPlantById(plantId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Tanaman") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is PlantUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PlantUiState.Error -> {
                val message = (uiState as PlantUiState.Error).message
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = message, color = Color.Red)
                }
            }
            is PlantUiState.Success -> {
                val plant = (uiState as PlantUiState.Success).plant

                // ✅ Logika untuk menentukan apakah sudah waktunya panen
                val harvestTimestamp = plant.plantingTime + TimeUnit.DAYS.toMillis(plant.harvestTime.toLong())
                val harvestDate = Calendar.getInstance().apply { timeInMillis = harvestTimestamp }
                val today = Calendar.getInstance()

                // Bandingkan hanya berdasarkan hari, abaikan jam/menit/detik
                val isHarvestTime = today[Calendar.YEAR] > harvestDate[Calendar.YEAR] ||
                        (today[Calendar.YEAR] == harvestDate[Calendar.YEAR] && today[Calendar.DAY_OF_YEAR] >= harvestDate[Calendar.DAY_OF_YEAR])

                val formattedHarvestDate = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(harvestDate.time)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Gambar Tanaman
                    plant.imageUrl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Gambar ${plant.plantName}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Nama Tanaman dan Tombol Edit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Sell, contentDescription = "Tag Nama", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Nama tanaman", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(plant.plantName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                        Button(
                            onClick = { navController.navigate("edit_plant/${plant.id}") },
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            Text("Edit")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Kartu Kalender Panen
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F5E9)) // Warna hijau muda
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Kalender Tanaman",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = "Jadwal Panen")
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = formattedHarvestDate,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Waktu Panen ${plant.plantName}!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // ✅ Tombol Panen Kondisional
                    if (isHarvestTime) {
                        Button(
                            onClick = { viewModel.harvestPlant(plant, navController) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Panen Sekarang!", fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}