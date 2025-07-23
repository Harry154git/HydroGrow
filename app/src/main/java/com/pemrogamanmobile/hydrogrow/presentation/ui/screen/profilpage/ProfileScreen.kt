package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.profilpage

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.profilpage.ProfileViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import com.pemrogamanmobile.hydrogrow.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogoutSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // ✅ State lokal untuk menampung input tidak lagi diperlukan
    // var name by rememberSaveable { mutableStateOf("") }

    // Tampilkan notifikasi saat error
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    if (state.isLoading && state.user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = state.user?.photoUrl,
                contentDescription = "Profile Photo",
                placeholder = painterResource(id = R.drawable.ic_notification_logo),
                error = painterResource(id = R.drawable.ic_notification_logo),
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ✅ TextField untuk Nama dibuat read-only
            OutlinedTextField(
                value = state.user?.name ?: "Tidak ada nama",
                onValueChange = { /* Tidak melakukan apa-apa */ },
                label = { Text("Nama") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.user?.email ?: "Tidak ada email",
                onValueChange = {},
                label = { Text("Email") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ Tombol "Simpan Perubahan" DIHAPUS

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Kembali")
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    viewModel.logout()
                    onLogoutSuccess()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border = BorderStroke(1.dp, Color.Red)
            ) {
                Text("Logout")
            }
        }
    }
}