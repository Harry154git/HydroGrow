package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Straighten
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
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.PlantCard
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage.GardenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenScreen(
    gardenId: String,
    viewModel: GardenViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(gardenId) {
        viewModel.loadGardenById(gardenId)
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Error: ${uiState.error}")
        }
    } else {
        uiState.garden?.let { garden ->
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(garden.gardenName, fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        AsyncImage(
                            model = garden.imageUrl,
                            contentDescription = garden.gardenName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Button(
                            onClick = { navController.navigate("edit_garden/$gardenId") },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))
                        ) {
                            Text("Edit")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(
                        icon = { Icon(Icons.Default.Straighten, contentDescription = "Ukuran") },
                        label = "Ukuran",
                        value = "${garden.gardenSize} m²"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(
                        icon = { Icon(Icons.Default.LocalOffer, contentDescription = "Tipe") },
                        label = "Tipe",
                        value = garden.hydroponicType
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tanamanmu",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        // ✅ DIPERBARUI: Navigasi ke halaman tambah tanaman
                        TextButton(onClick = { navController.navigate("add_plant") }) {
                            Text("Tambah Tanaman", color = Color(0xFF6B8E23))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp), // Beri ketinggian agar bisa di-scroll di dalam Column
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        content = {
                            items(uiState.plants) { plant ->
                                PlantCard(
                                    imageUrl = plant.imageUrl,
                                    name = plant.plantName,
                                    harvestTime = plant.harvestTime.toIntOrNull() ?: 0,
                                    // Navigasi ke detail tanaman sudah benar
                                    onClick = { navController.navigate("plant_screen/${plant.id}") }
                                )
                            }
                        }
                    )
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Kebun tidak ditemukan.")
            }
        }
    }
}

@Composable
fun InfoRow(icon: @Composable () -> Unit, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        Text("$label: $value", fontSize = 16.sp)
    }
}