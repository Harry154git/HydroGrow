package com.pemrogamanmobile.hydrogrow.profilpage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pemrogamanmobile.hydrogrow.R
import com.pemrogamanmobile.hydrogrow.data.DataSource


@Composable
fun ProfilScreen(onBackToHome: () -> Unit) {
    val context = LocalContext.current
    val dataSource = remember { DataSource.getInstance(context) }
    val profile = dataSource.getCurrentUser()

    var isEditing by remember { mutableStateOf(false) }

    if (profile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tidak ada pengguna yang sedang login.")
        }
        return
    }

    var name by remember { mutableStateOf(profile.name) }
    var email by remember { mutableStateOf(profile.email) }
    var password by remember { mutableStateOf(profile.password) }
    var phone by remember { mutableStateOf(profile.phoneNumber) }
    var address by remember { mutableStateOf(profile.address) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(R.drawable.user),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.Center)
            )

            TextButton(
                onClick = { onBackToHome() },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text("Kembali", color = Color.Blue)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProfileTextField("Nama", name, isEditing) { name = it }
        ProfileTextField("Email", email, isEditing) { email = it }
        ProfileTextField("Password", password, isEditing) { password = it }
        ProfileTextField("No. Telepon", phone, isEditing) { phone = it }
        ProfileTextField("Alamat", address, isEditing) { address = it }

        Spacer(modifier = Modifier.height(24.dp))

        if (isEditing) {
            Button(onClick = {
                val updatedUser = profile.copy(
                    email = email,
                    password = password,
                    name = name,
                    phoneNumber = phone,
                    address = address
                )
                dataSource.registerUser(updatedUser)
                isEditing = false
            }) {
                Text("Simpan")
            }
        } else {
            Button(onClick = { isEditing = true }) {
                Text("Edit")
            }
        }
    }
}

@Composable
fun ProfileTextField(label: String, value: String, editable: Boolean, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        if (editable) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}