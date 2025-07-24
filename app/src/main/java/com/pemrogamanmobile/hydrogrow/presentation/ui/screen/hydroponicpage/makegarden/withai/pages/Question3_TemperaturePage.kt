package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withai.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
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

// Map untuk mengasosiasikan pilihan UI dengan teks prompt yang deskriptif untuk AI
private val temperatureOptions = mapOf(
    "Daerah Sejuk" to "Suhu cenderung sejuk seperti di pegunungan atau ruangan ber-AC (di bawah 22°C).",
    "Suhu Ideal/Hangat" to "Suhu ideal atau hangat seperti suhu ruangan pada umumnya (sekitar 23-29°C).",
    "Daerah Panas" to "Suhu cenderung panas seperti di dataran rendah atau pesisir (di atas 30°C)."
)

@Composable
fun TemperaturePage(
    paddingValues: PaddingValues,
    selectedTemperature: String,
    onTemperatureSelected: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Suhu & Cuaca", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Bagaimana biasanya cuaca di daerahmu",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Opsi Pilihan Ganda
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SelectableOptionCard(
                title = "Daerah Sejuk",
                subtitle = "Seperti di pegunungan, sering berkabut, atau di dalam ruangan yang selalu ber-AC.",
                icon = Icons.Default.Landscape,
                isSelected = selectedTemperature == temperatureOptions["Daerah Sejuk"],
                onClick = { onTemperatureSelected(temperatureOptions["Daerah Sejuk"]!!) }
            )
            SelectableOptionCard(
                title = "Suhu Ideal/Hangat",
                subtitle = "Seperti suhu ruangan yang nyaman, tidak terlalu panas maupun dingin.",
                icon = Icons.Default.Home,
                isSelected = selectedTemperature == temperatureOptions["Suhu Ideal/Hangat"],
                onClick = { onTemperatureSelected(temperatureOptions["Suhu Ideal/Hangat"]!!) }
            )
            SelectableOptionCard(
                title = "Daerah Panas",
                subtitle = "Seperti di dataran rendah atau pesisir yang mataharinya cukup terik di siang hari.",
                icon = Icons.Default.WbSunny,
                isSelected = selectedTemperature == temperatureOptions["Daerah Panas"],
                onClick = { onTemperatureSelected(temperatureOptions["Daerah Panas"]!!) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Tombol Navigasi
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
                enabled = selectedTemperature.isNotBlank(), // Aktif jika sudah memilih
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))
            ) { Text("Selanjutnya") }
        }
    }
}

// Composable ini bisa dipindahkan ke file terpisah jika ingin digunakan di banyak tempat
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