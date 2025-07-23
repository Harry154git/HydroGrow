package com.pemrogamanmobile.hydrogrow.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pemrogamanmobile.hydrogrow.R
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ProfileCard(
    name: String,
    photoUrl: String?,
    onProfileClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProfileClick() }
        // Menghapus padding agar menempel di tepi sesuai layout HomeScreen
    ) {
        // Kolom untuk teks sambutan dan nama pengguna
        Column(
            modifier = Modifier.weight(1f) // Memberi sisa ruang untuk gambar
        ) {
            Text(
                text = "Selamat Datang,",
                style = MaterialTheme.typography.bodyLarge // Teks sedikit lebih besar
            )
            Text(
                // Menambahkan "!" agar sesuai desain
                text = "$name!",
                // Style dibuat lebih besar dan tebal sesuai desain
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Gambar profil dari URL atau default jika tidak ada
        val imageModifier = Modifier
            .size(60.dp) // Ukuran gambar disesuaikan agar pas
            .clip(CircleShape)

        if (!photoUrl.isNullOrEmpty()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Foto Profil",
                contentScale = ContentScale.Crop, // Memastikan gambar mengisi lingkaran
                modifier = imageModifier
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_info), // Pastikan drawable ini ada
                contentDescription = "Foto Profil Default",
                modifier = imageModifier
            )
        }
    }
}