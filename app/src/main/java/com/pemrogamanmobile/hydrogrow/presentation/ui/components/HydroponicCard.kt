package com.pemrogamanmobile.hydrogrow.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pemrogamanmobile.hydrogrow.R // Pastikan R di-import dengan benar
import com.pemrogamanmobile.hydrogrow.domain.model.Garden

@Composable
fun HydroponicCard(
    garden: Garden, // Diubah untuk menerima objek Garden langsung
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            // Padding sekarang diatur oleh LazyColumn di HomeScreen
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp), // Sudut lebih bulat sesuai desain
        colors = CardDefaults.cardColors(
            // Warna latar belakang hijau muda sesuai desain
            containerColor = Color(0xFFE6F5E3)
        )
    ) {
        Column {
            // Bagian Gambar Kebun
            AsyncImage(
                model = garden.imageUrl,
                contentDescription = "Gambar ${garden.gardenName}",
                contentScale = ContentScale.Crop, // Agar gambar mengisi area tanpa distorsi
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) // Hanya sudut atas gambar
            )

            // Bagian Detail Teks di bawah gambar
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Jarak antar elemen teks
            ) {
                // Nama Kebun
                Text(
                    text = garden.gardenName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Detail Ukuran
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notification_logo), // Ganti dengan ikonmu
                        contentDescription = "Ikon Ukuran",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ukuran",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    // Catatan: Model domain memiliki 'gardenSize' sebagai Double.
                    // Di sini kita format menjadi string "m²".
                    Text(
                        text = "${garden.gardenSize} m²",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Detail Tipe Hidroponik
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notification_logo), // Ganti dengan ikonmu
                        contentDescription = "Ikon Tipe",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tipe",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = garden.hydroponicType,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}