package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withai.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // <-- IMPORT INI
import androidx.compose.foundation.verticalScroll // <-- IMPORT INI
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Map untuk mengasosiasikan pilihan UI dengan teks prompt untuk AI
private val lightConditionOptions = mapOf(
    "Terang Benderang" to "Pencahayaan penuh, lebih dari 6 jam sinar matahari langsung.",
    "Cukup Terang" to "Pencahayaan sebagian, kadang teduh dan kadang terkena matahari.",
    "Teduh" to "Pencahayaan teduh, sebagian besar di tempat teduh atau dalam ruangan."
)

@Composable
fun LightConditionPage(
    paddingValues: PaddingValues,
    selectedCondition: String,
    onConditionSelected: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    // Column utama untuk menahan konten scroll dan tombol navigasi
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Column ini akan berisi semua konten yang bisa di-scroll
        Column(
            modifier = Modifier
                .weight(1f) // <-- 1. Membuat Column ini mengisi ruang yang tersedia
                .verticalScroll(rememberScrollState()), // <-- 2. Membuatnya bisa di-scroll
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Kondisi Cahaya", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Kondisi Cahaya",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFF9A825) // Warna kuning matahari
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Seberapa banyak sinar matahari yang didapat lokasi kebunmu?",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Opsi Pilihan Ganda
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectableOptionCard(
                    title = "Terang Benderang",
                    subtitle = "Kena sinar matahari langsung lebih dari 6 jam.",
                    icon = Icons.Default.WbSunny,
                    isSelected = selectedCondition == lightConditionOptions["Terang Benderang"],
                    onClick = { onConditionSelected(lightConditionOptions["Terang Benderang"]!!) }
                )
                SelectableOptionCard(
                    title = "Cukup Terang",
                    subtitle = "Kadang teduh, kadang terkena matahari.",
                    icon = Icons.Default.WbCloudy,
                    isSelected = selectedCondition == lightConditionOptions["Cukup Terang"],
                    onClick = { onConditionSelected(lightConditionOptions["Cukup Terang"]!!) }
                )
                SelectableOptionCard(
                    title = "Teduh",
                    subtitle = "Lebih banyak di tempat teduh atau dalam ruangan.",
                    icon = Icons.Default.Cloud,
                    isSelected = selectedCondition == lightConditionOptions["Teduh"],
                    onClick = { onConditionSelected(lightConditionOptions["Teduh"]!!) }
                )
            }
            // Tambahkan spacer di bawah agar ada jarak sebelum akhir scroll
            Spacer(modifier = Modifier.height(16.dp))
        }


        // Spacer(modifier = Modifier.weight(1f)) <-- 3. HAPUS SPACER INI

        // Tombol Navigasi tetap di bawah karena Column konten di atasnya menggunakan weight(1f)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onPrevious,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8F9779))
            ) { Text("Sebelumnya") }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedCondition.isNotBlank(), // Aktif jika sudah memilih
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))
            ) { Text("Selanjutnya") }
        }
    }
}

// Composable SelectableOptionCard tidak perlu diubah
@Composable
private fun SelectableOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFD0F0C0) else MaterialTheme.colorScheme.surfaceVariant
    val border = if (isSelected) BorderStroke(2.dp, Color(0xFF6B8E23)) else null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = border,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
            }
        }
    }
}