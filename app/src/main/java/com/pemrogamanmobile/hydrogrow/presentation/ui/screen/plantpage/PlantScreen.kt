package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pemrogamanmobile.hydrogrow.presentation.uistate.PlantUiState
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage.PlantViewModel

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
                    // ✅ Gunakan properti 'plantName' dari model domain
                    Text("Nama: ${plant.plantName}", style = MaterialTheme.typography.headlineSmall)
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Kembali")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                plant.imageUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "Plant Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        navController.navigate("edit_plant/${plant.id}")
                    }) {
                        Text("Edit")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    navController.navigate("chatbot")
                }) {
                    Text("Tanya tentang tanaman ini?")
                }
            }
        }
    }
}