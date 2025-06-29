package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.HydroponicCard
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.PlantCard
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.ProfileCard
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.home.HomeViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToProfile: () -> Unit,
    navigateToMakeGardenInput: () -> Unit,
    navigateToGarden: (String) -> Unit,
    navigateToPlant: (String) -> Unit,
    navigateToAddPlant: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Listen lifecycle resume untuk refresh data
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            uiState.user?.let { user ->
                ProfileCard(
                    photoUrl = user.photoUrl,
                    name = user.name,
                    onProfileClick = navigateToProfile
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Kebunmu",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Buat Kebun Baru",
                    color = Color(0xFF4CAF50), // Hijau
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier
                        .clickable(onClick = navigateToMakeGardenInput)
                )
            }
        }

        items(uiState.gardens) { garden ->
            HydroponicCard(
                name = garden.name,
                size = garden.size.toString(),
                type = garden.hydroponicType,
                onClick = { navigateToGarden(garden.id) }
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Tanamanmu",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Tambah Tanaman",
                    color = Color(0xFF4CAF50), // Hijau
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier
                        .clickable(onClick = navigateToAddPlant)
                )
            }
        }

        item {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(uiState.plants) { plant ->
                    PlantCard(
                        imageUrl = plant.imageUrl,
                        name = plant.name,
                        status = plant.harvestStatus,
                        onClick = { navigateToPlant(plant.id) }
                    )
                }
            }
        }
    }
}