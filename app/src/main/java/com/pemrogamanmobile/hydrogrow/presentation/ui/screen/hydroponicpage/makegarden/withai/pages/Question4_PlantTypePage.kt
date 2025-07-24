package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withai.pages

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // <-- IMPORT INI
import androidx.compose.foundation.verticalScroll // <-- IMPORT INI
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pemrogamanmobile.hydrogrow.R

private val plantTypeOptions = mapOf(
    "Sayuran Daun" to "Saya ingin menanam sayuran daun seperti selada, kangkung, atau bayam.",
    "Buah-buahan" to "Saya ingin menanam tanaman yang menghasilkan buah seperti stroberi atau tomat.",
    "Bumbu Dapur" to "Saya ingin menanam bumbu dapur seperti cabai, seledri, atau tanaman herbal."
)

@Composable
fun PlantTypePage(
    paddingValues: PaddingValues,
    selectedPlantType: String,
    isRecommendationChecked: Boolean,
    onPlantTypeSelected: (String) -> Unit,
    onRecommendationToggle: (Boolean) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val isNextEnabled = isRecommendationChecked || selectedPlantType.isNotBlank()

    // Column luar sebagai container utama
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Column dalam untuk konten yang bisa di-scroll
        Column(
            modifier = Modifier
                .weight(1f) // <-- 1. Mengisi ruang yang tersedia
                .verticalScroll(rememberScrollState()), // <-- 2. Membuat konten bisa di-scroll
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Jenis Tanaman", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_notification_logo), // Ganti dengan ID drawable Anda
                contentDescription = "Ilustrasi Tanaman",
                modifier = Modifier.height(80.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Apa sih yang paling ingin kamu tanam?",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Grid 2x2 untuk pilihan
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SelectableGridItem(
                        modifier = Modifier.weight(1f),
                        title = "Sayuran Daun",
                        subtitle = "Contoh: Selada, Kangkung, Bayam",
                        imageRes = R.drawable.ic_notification_logo, // Ganti dengan ID drawable Anda
                        isSelected = !isRecommendationChecked && selectedPlantType == plantTypeOptions["Sayuran Daun"],
                        onClick = { onPlantTypeSelected(plantTypeOptions["Sayuran Daun"]!!) }
                    )
                    SelectableGridItem(
                        modifier = Modifier.weight(1f),
                        title = "Buah-buahan",
                        subtitle = "Contoh: Stroberi, Tomat",
                        imageRes = R.drawable.ic_notification_logo, // Ganti dengan ID drawable Anda
                        isSelected = !isRecommendationChecked && selectedPlantType == plantTypeOptions["Buah-buahan"],
                        onClick = { onPlantTypeSelected(plantTypeOptions["Buah-buahan"]!!) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SelectableGridItem(
                        modifier = Modifier.weight(1f),
                        title = "Bumbu Dapur",
                        subtitle = "Contoh: Cabai, Seledri, Herbal",
                        imageRes = R.drawable.ic_notification_logo, // Ganti dengan ID drawable Anda
                        isSelected = !isRecommendationChecked && selectedPlantType == plantTypeOptions["Bumbu Dapur"],
                        onClick = { onPlantTypeSelected(plantTypeOptions["Bumbu Dapur"]!!) }
                    )
                    RecommendationGridItem(
                        modifier = Modifier.weight(1f),
                        isSelected = isRecommendationChecked,
                        onClick = { onRecommendationToggle(!isRecommendationChecked) }
                    )
                }
            }
            // Spacer untuk memberi jarak di akhir scroll
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Spacer(modifier = Modifier.weight(1f)) // <-- 3. SPACER INI DIHAPUS

        // Tombol Navigasi tetap di bawah layar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onPrevious, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8F9779))) { Text("Sebelumnya") }
            Button(onClick = onNext, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp), enabled = isNextEnabled, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))) { Text("Selanjutnya") }
        }
    }
}

// Composable SelectableGridItem dan RecommendationGridItem tidak perlu diubah.
@Composable
private fun SelectableGridItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    @DrawableRes imageRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFD0F0C0) else MaterialTheme.colorScheme.surface
    val border = if (isSelected) BorderStroke(2.dp, Color(0xFF6B8E23)) else null

    Card(
        modifier = modifier.aspectRatio(1f), // Membuat kartu menjadi persegi
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = border,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = imageRes), contentDescription = title, modifier = Modifier.weight(1f).padding(4.dp), contentScale = ContentScale.Fit)
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun RecommendationGridItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFD0F0C0) else Color(0xFFE0EACF)
    val contentColor = if (isSelected) Color(0xFF3A5F0B) else MaterialTheme.colorScheme.onSurface
    val border = if (isSelected) BorderStroke(2.dp, Color(0xFF6B8E23)) else null

    Card(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor, contentColor = contentColor),
        border = border,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = Icons.Default.HelpOutline, contentDescription = "Beri Saya Rekomendasi!", modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Beri Saya Rekomendasi!", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}