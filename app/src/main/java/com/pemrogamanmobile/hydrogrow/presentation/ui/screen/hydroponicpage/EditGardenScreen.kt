package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pemrogamanmobile.hydrogrow.domain.model.Garden
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage.EditGardenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGardenScreen(
    gardenId: String,
    navController: NavController,
    viewModel: EditGardenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // ✅ State untuk mengontrol dialog konfirmasi hapus
    val showDeleteDialog = remember { mutableStateOf(false) }

    LaunchedEffect(gardenId) {
        viewModel.loadGarden(gardenId)
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            Toast.makeText(context, "Error: ${uiState.error}", Toast.LENGTH_LONG).show()
            viewModel.resetError()
        }
    }

    // Gunakan Box untuk menampung layar utama dan dialog overlay
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            // ✅ Terapkan efek blur jika dialog muncul
            modifier = Modifier.blur(radius = if (showDeleteDialog.value) 8.dp else 0.dp),
            topBar = {
                TopAppBar(
                    title = { Text("Edit Kebun", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            when {
                uiState.isLoading && uiState.garden == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.garden == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Kebun tidak ditemukan atau gagal dimuat.")
                    }
                }
                else -> {
                    uiState.garden?.let { garden ->
                        EditGardenForm(
                            garden = garden,
                            paddingValues = paddingValues,
                            onDeleteClick = { showDeleteDialog.value = true }, // ✅ Tampilkan dialog saat tombol hapus diklik
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
            }
        }

        // ✅ Tampilkan dialog jika state-nya true
        if (showDeleteDialog.value) {
            uiState.garden?.let { garden ->
                DeleteConfirmationDialog(
                    onDismiss = { showDeleteDialog.value = false },
                    onConfirm = {
                        viewModel.deleteGarden(garden)
                        showDeleteDialog.value = false
                        navController.popBackStack() // Kembali ke halaman detail
                        navController.popBackStack() // Kembali ke halaman list kebun
                    }
                )
            }
        }
    }
}

@Composable
fun EditGardenForm(
    garden: Garden,
    paddingValues: PaddingValues,
    onDeleteClick: () -> Unit,
    viewModel: EditGardenViewModel,
    navController: NavController
) {
    var namaKebun by remember { mutableStateOf(garden.gardenName) }
    var panjangKebun by remember { mutableStateOf("") }
    var lebarKebun by remember { mutableStateOf("") }
    var tipeHidroponik by remember { mutableStateOf(garden.hydroponicType) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = imageUri ?: garden.imageUrl,
            contentDescription = "Foto Kebun",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Ubah Foto", textDecoration = TextDecoration.Underline)
        }
        Spacer(modifier = Modifier.height(16.dp))

        FormTextField(
            label = "Nama Kebun",
            value = namaKebun,
            onValueChange = { namaKebun = it }
        )
        FormTextField(
            label = "Panjang Kebun (m)",
            value = panjangKebun,
            onValueChange = { panjangKebun = it },
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Lebar Kebun (m)",
            value = lebarKebun,
            onValueChange = { lebarKebun = it },
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Tipe Hidroponik",
            value = tipeHidroponik,
            onValueChange = { tipeHidroponik = it }
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8F9779))
            ) { Text("Batal") }
            Button(
                onClick = {
                    val p = panjangKebun.toDoubleOrNull() ?: 0.0
                    val l = lebarKebun.toDoubleOrNull() ?: 0.0
                    val newSize = if (p > 0 && l > 0) p * l else garden.gardenSize
                    val updatedGarden = garden.copy(
                        gardenName = namaKebun,
                        gardenSize = newSize,
                        hydroponicType = tipeHidroponik
                    )
                    viewModel.updateGarden(updatedGarden)
                    navController.popBackStack()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))
            ) { Text("Simpan") }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDeleteClick, // ✅ Panggil lambda untuk menampilkan dialog
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))
        ) { Text("Hapus Kebun") }
    }
}

@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6B8E23),
                unfocusedBorderColor = Color.Gray,
                unfocusedContainerColor = Color(0xFFF0FFF0).copy(alpha = 0.5f),
                focusedContainerColor = Color(0xFFF0FFF0).copy(alpha = 0.5f)
            ),
            placeholder = { Text(label, color = Color.Gray) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ✅ COMPOSABLE BARU UNTUK DIALOG KONFIRMASI
@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .clickable(enabled = false) {}, // Mencegah klik di card menutup dialog
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Menghapus kebun juga akan menghapus semua tanaman di dalamnya. Kamu yakin?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onDismiss, // Tombol Batal
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8F9779))
                    ) { Text("Batal") }
                    Button(
                        onClick = onConfirm, // Tombol Hapus
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))
                    ) { Text("Hapus") }
                }
            }
        }
    }
}