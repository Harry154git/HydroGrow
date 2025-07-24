package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.HydroponicCard
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.ProfileCard // Pastikan impor ini ada
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.home.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToMakeGardenInput: () -> Unit,
    navigateToAIAssistedGarden: () -> Unit,
    navigateToGarden: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    var showAddOptionsDialog by remember { mutableStateOf(false) }

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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.blur(radius = if (showAddOptionsDialog) 8.dp else 0.dp),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddOptionsDialog = true },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Kebun"
                    )
                }
            }
        ) { paddingValues ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp) // Beri padding atas
                ) {
                    item {
                        uiState.user?.let { user ->
                            // ✅ MODIFIKASI PEMANGGILAN ProfileCard
                            ProfileCard(
                                name = user.name ?: "Pengguna",
                                photoUrl = user.photoUrl,
                                cupAmount = uiState.cupAmount, // Teruskan data panen
                                onProfileClick = { viewModel.logViewProfile() }
                            )
                        }
                    }

                    item {
                        // ✅ KEMBALIKAN BAGIAN INI KE BENTUK SEMULA
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Kebunmu",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🪴",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }

                    if (uiState.gardens.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Kamu belum punya kebun",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Ayo buat kebun pertamamu dengan menekan tombol '+'",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        items(uiState.gardens) { garden ->
                            HydroponicCard(
                                garden = garden,
                                onClick = { navigateToGarden(garden.id) }
                            )
                        }
                    }

                    uiState.error?.let { error ->
                        item {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.fillMaxWidth().padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        if (showAddOptionsDialog) {
            AddGardenOptionsDialog(
                onDismiss = { showAddOptionsDialog = false },
                onManualClick = {
                    showAddOptionsDialog = false
                    navigateToMakeGardenInput()
                },
                onAIClick = {
                    showAddOptionsDialog = false
                    navigateToAIAssistedGarden()
                }
            )
        }
    }
}


@Composable
fun AddGardenOptionsDialog(
    onDismiss: () -> Unit,
    onManualClick: () -> Unit,
    onAIClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 100.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChoiceButton(
                text = "Saya sudah memahami kondisi kebun saya.",
                icon = Icons.Default.EnergySavingsLeaf,
                onClick = onManualClick
            )
            ChoiceButton(
                text = "Saya butuh bantuan AI untuk rekomendasi kebun saya.",
                icon = Icons.Default.SmartToy,
                onClick = onAIClick
            )
        }
    }
}

@Composable
fun ChoiceButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = CircleShape,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD0F0C0).copy(alpha = 0.95f),
            contentColor = Color.Black
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, fontSize = 14.sp)
        }
    }
}