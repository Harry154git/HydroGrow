package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.foundation.Image
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.layout.ContentScale

@Composable
fun EditGardenScreen(
    gardenId: String,
    navController: NavController,
    viewModel: EditGardenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Toast untuk error
    LaunchedEffect(uiState.error) {
        if (uiState.error != null && uiState.garden != null) {
            Toast.makeText(context, "Error: ${uiState.error}", Toast.LENGTH_LONG).show()
            viewModel.resetError()
        }
    }

    // Load garden pertama kali
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

    var editableKebun by remember { mutableStateOf(kebun) }
    var isEditing by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
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

        InfoRow("Nama Kebun:", editableKebun.name, isEditing) {
            editableKebun = editableKebun.copy(name = it)
        }
        InfoRow("Luas Kebun (m²):", editableKebun.size.toString(), isEditing) {
            editableKebun = editableKebun.copy(size = it.toDoubleOrNull() ?: editableKebun.size)
        }
        InfoRow("Tipe Hidroponik:", editableKebun.hydroponicType, isEditing) {
            editableKebun = editableKebun.copy(hydroponicType = it)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { isEditing = true },
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            ) {
                Text("Edit")
            }
            Button(
                onClick = {
                    viewModel.updateGarden(editableKebun)
                    isEditing = false
                    navController.popBackStack()
                },
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            ) {
                Text("Simpan")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.deleteGarden(editableKebun)
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
