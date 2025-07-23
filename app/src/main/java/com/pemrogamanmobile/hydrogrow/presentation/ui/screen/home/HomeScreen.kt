package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.HydroponicCard
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.ProfileCard
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.home.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToMakeGardenInput: () -> Unit,
    navigateToGarden: (String) -> Unit,
    navigateToPlant: (String) -> Unit, // Parameter ini tetap ada sesuai kodemu
    navigateToAddPlant: () -> Unit // Parameter ini tetap ada sesuai kodemu
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Listener untuk me-refresh data saat layar kembali ditampilkan (ON_RESUME)
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToMakeGardenInput,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Kebun"
                )
            }
        }
    ) { paddingValues ->

        // Menampilkan indikator loading di tengah layar jika data sedang dimuat
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Tampilkan konten utama jika loading selesai
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Bagian 1: Kartu Profil Pengguna (Panggilan Diperbaiki)
                item {
                    uiState.user?.let { user ->
                        // Panggilan disesuaikan dengan ProfileCard.kt
                        ProfileCard(
                            name = user.name ?: "Pengguna",
                            photoUrl = user.photoUrl,
                            onProfileClick = {
                                viewModel.logViewProfile()
                                // TODO: Tambahkan navigasi ke halaman profil jika ada
                            }
                        )
                    }
                }

                // Bagian 2: Judul "Kebunmu"
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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

                // Bagian 3: Menampilkan daftar kebun atau pesan jika kosong
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
                    // Daftar Kartu Kebun
                    items(uiState.gardens) { garden ->
                        HydroponicCard(
                            garden = garden,
                            onClick = { navigateToGarden(garden.id) }
                        )
                    }
                }

                // Menampilkan pesan error di bagian bawah jika ada
                uiState.error?.let { error ->
                    item {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}