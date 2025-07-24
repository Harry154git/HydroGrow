package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withai.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LandSizePage(
    paddingValues: PaddingValues,
    panjang: String,
    lebar: String,
    isRecommendationChecked: Boolean,
    onPanjangChange: (String) -> Unit,
    onLebarChange: (String) -> Unit,
    onRecommendationToggle: (Boolean) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    // Tombol 'Selanjutnya' aktif jika user meminta rekomendasi ATAU jika kedua field terisi
    val isNextEnabled = isRecommendationChecked || (panjang.isNotBlank() && lebar.isNotBlank())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ukuran Lahan", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = Icons.Default.Straighten,
            contentDescription = "Ukuran Lahan",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Yuk, kita ukur luas lahan yang kamu punya.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Input manual untuk panjang dan lebar ---
        Column(
            // Kurangi alpha (buat lebih transparan) jika rekomendasi dipilih
            modifier = Modifier.alpha(if (isRecommendationChecked) 0.5f else 1f)
        ) {
            OutlinedTextField(
                value = panjang,
                onValueChange = onPanjangChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Panjang (meter)") },
                placeholder = { Text("Ketikkan angka saja") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                enabled = !isRecommendationChecked // Nonaktifkan jika minta rekomendasi
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = lebar,
                onValueChange = onLebarChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Lebar (meter)") },
                placeholder = { Text("Ketikkan angka saja") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                enabled = !isRecommendationChecked // Nonaktifkan jika minta rekomendasi
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Tombol untuk meminta rekomendasi AI ---
        val recommendationButtonColor = if (isRecommendationChecked) Color(0xFFD0F0C0) else MaterialTheme.colorScheme.surfaceVariant
        val recommendationBorder = if (isRecommendationChecked) BorderStroke(2.dp, Color(0xFF6B8E23)) else null

        Card(
            onClick = { onRecommendationToggle(!isRecommendationChecked) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = recommendationButtonColor),
            border = recommendationBorder
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.HelpOutline, contentDescription = "Rekomendasi")
                Spacer(modifier = Modifier.width(16.dp))
                Text("Beri Saya Rekomendasi", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- Tombol Navigasi ---
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
                enabled = isNextEnabled, // Gunakan state yang sudah dihitung
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))
            ) { Text("Selanjutnya") }
        }
    }
}