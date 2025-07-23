package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage.GardenViewModel
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.PlantCard

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

    val scrollState = rememberScrollState()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (uiState.error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Error: ${uiState.error}")
        }
    } else {
        uiState.garden?.let { garden ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        // ✅ Gunakan 'gardenName' dari model domain
                        text = garden.gardenName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Kembali")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))


                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { navController.navigate("edit_garden/${gardenId}") }) {
                        Text("Edit")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    navController.navigate("chatbot")
                }) {
                    Text("Tanya tentang hidroponik ini?")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tanaman di Kebun Ini",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(uiState.plants) { plant ->
                        PlantCard(
                            imageUrl = plant.imageUrl,
                            // ✅ Gunakan 'plantName' dari model domain
                            name = plant.plantName,
                            // ✅ Ganti 'harvestStatus' dengan 'harvestTime'
                            status = plant.harvestTime,
                            onClick = { navController.navigate("plant_screen/${plant.id}") }
                        )
                    }
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