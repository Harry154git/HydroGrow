package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withnoai

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage.makegarden.withnoai.MakeGardenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeGardenScreen(
    navController: NavController,
    viewModel: MakeGardenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // State untuk menampung input dari form
    var namaKebun by remember { mutableStateOf("") }
    var panjangKebun by remember { mutableStateOf("") }
    var lebarKebun by remember { mutableStateOf("") }
    var tipeHidroponik by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher untuk memilih gambar dari galeri
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Menangani side-effect setelah proses di ViewModel selesai
    LaunchedEffect(key1 = uiState) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Kebun berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            viewModel.resetState()
        }
        if (uiState.error != null) {
            Toast.makeText(context, "Error: ${uiState.error}", Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Kebun", fontWeight = FontWeight.Bold) },
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Placeholder atau gambar yang dipilih
                AsyncImage(
                    model = imageUri ?: "https://firebasestorage.googleapis.com/v0/b/hydrogrow-33a95.appspot.com/o/garden_images%2Fplaceholder.png?alt=media&token=42d13a77-94d7-4648-a9c1-4a1827581e2b",
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

                // Input Fields
                FormTextField(label = "Nama Kebun", value = namaKebun, onValueChange = { namaKebun = it })
                FormTextField(label = "Panjang Kebun (m)", value = panjangKebun, onValueChange = { panjangKebun = it }, keyboardType = KeyboardType.Number)
                FormTextField(label = "Lebar Kebun (m)", value = lebarKebun, onValueChange = { lebarKebun = it }, keyboardType = KeyboardType.Number)
                FormTextField(label = "Tipe Hidroponik", value = tipeHidroponik, onValueChange = { tipeHidroponik = it })
                Spacer(modifier = Modifier.height(32.dp))

                // Tombol Simpan
                Button(
                    onClick = {
                        viewModel.createGarden(
                            gardenName = namaKebun,
                            panjang = panjangKebun,
                            lebar = lebarKebun,
                            hydroponicType = tipeHidroponik,
                            imageUri = imageUri
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23)),
                    enabled = !uiState.isLoading
                ) {
                    Text("Simpan Kebun", modifier = Modifier.padding(vertical = 8.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Tampilkan loading indicator di tengah layar
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}


@Composable
private fun FormTextField(
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
            placeholder = { Text(label.split(" ")[0], color = Color.Gray) } // Contoh: "Panjang", "Nama"
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}