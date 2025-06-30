package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage.EditGardenViewModel

@Composable
fun EditGardenScreen(
    gardenId: String,
    navController: NavController,
    viewModel: EditGardenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        if (uiState.error != null && uiState.garden != null) {
            Toast.makeText(context, "Error: ${uiState.error}", Toast.LENGTH_LONG).show()
            viewModel.resetError()
        }
    }

    LaunchedEffect(gardenId) {
        viewModel.loadGarden(gardenId)
    }

    val kebun = uiState.garden

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.error != null && kebun == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Terjadi kesalahan saat memuat kebun: ${uiState.error}")
        }
        return
    }

    if (kebun == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Kebun tidak ditemukan.")
        }
        return
    }

    // ✅ Local value yang survive rotasi
    var localName by rememberSaveable { mutableStateOf("") }
    var localSize by rememberSaveable { mutableStateOf("") }
    var localType by rememberSaveable { mutableStateOf("") }
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var isInitialized by rememberSaveable { mutableStateOf(false) }

    // ✅ Inisialisasi hanya sekali
    LaunchedEffect(Unit) {
        if (!isInitialized) {
            localName = kebun.name
            localSize = kebun.size.toString()
            localType = kebun.hydroponicType
            isInitialized = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Edit Kebun",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { navController.popBackStack() }) {
                Text("Kembali")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        InfoRow(
            "Nama Kebun:",
            if (isEditing) localName else kebun.name,
            isEditing
        ) { localName = it }

        InfoRow(
            "Luas Kebun (m²):",
            if (isEditing) localSize else kebun.size.toString(),
            isEditing
        ) { localSize = it }

        InfoRow(
            "Tipe Hidroponik:",
            if (isEditing) localType else kebun.hydroponicType,
            isEditing
        ) { localType = it }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (!isEditing) {
                        localName = kebun.name
                        localSize = kebun.size.toString()
                        localType = kebun.hydroponicType
                    }
                    isEditing = !isEditing
                },
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            ) {
                Text(if (isEditing) "Batal" else "Edit")
            }

            if (isEditing) {
                Button(
                    onClick = {
                        val updatedGarden = kebun.copy(
                            name = localName,
                            size = localSize.toDoubleOrNull() ?: kebun.size,
                            hydroponicType = localType
                        )
                        viewModel.updateGarden(updatedGarden)
                        isEditing = false
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Text("Simpan")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.deleteGarden(kebun)
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hapus Kebun", color = Color.White)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Bold)
        if (isEditing) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(value, modifier = Modifier.padding(4.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}