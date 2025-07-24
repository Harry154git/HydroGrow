package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withai.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.pemrogamanmobile.hydrogrow.domain.model.AiGardenPlan
import java.text.NumberFormat
import java.util.*

@Composable
fun ResultPage(
    paddingValues: PaddingValues,
    plan: AiGardenPlan,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ini Dia Rekomendasi Kebun Untukmu!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Icon(
            imageVector = Icons.Default.Yard, // Menggunakan ikon pot/tanaman
            contentDescription = "Rekomendasi Kebun",
            modifier = Modifier.size(64.dp),
            tint = Color(0xFFC07A56) // Warna terakota
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- KARTU RINGKASAN REKOMENDASI ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F5D0)) // Warna hijau sangat muda
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                RecommendationItem(
                    icon = Icons.Default.Eco,
                    label = "Tipe Sistem yang Cocok",
                    value = plan.hydroponicType
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                RecommendationItem(
                    icon = Icons.Default.Grass,
                    label = "Rekomendasi Tanaman",
                    value = if (plan.recommendedPlants.isNotEmpty()) plan.recommendedPlants.joinToString(", ") else "Sesuai Pilihanmu"
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                RecommendationItem(
                    icon = Icons.Default.Straighten,
                    label = "Estimasi Ukuran",
                    value = "${plan.landSizeM2} m²"
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                RecommendationItem(
                    icon = Icons.Default.Savings,
                    label = "Perkiraan Biaya",
                    value = formatCurrency(plan.estimatedCost)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ PENJELASAN DETAIL DARI AI
        Text(
            text = plan.displayText,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
            textAlign = TextAlign.Justify
        )

        Spacer(modifier = Modifier.weight(1f))

        // --- TOMBOL TAMBAHKAN KEBUN ---
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))
        ) {
            Text(
                text = "Tambahkan Kebun",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RecommendationItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = Color(0xFF556B2F) // Warna hijau tua
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    format.maximumFractionDigits = 0 // Tidak menampilkan desimal
    return format.format(amount)
}