package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withai.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrackChanges
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
private val goalOptions = mapOf(
    "Konsumsi Sendiri" to "Tujuan utama penanaman adalah untuk hobi dan konsumsi pribadi keluarga.",
    "Berbagi dengan Tetangga" to "Tujuan penanaman adalah untuk konsumsi pribadi dan sesekali berbagi dengan tetangga atau komunitas sekitar.",
    "Untuk Dijual Skala Kecil" to "Tujuan penanaman adalah untuk dijual dalam skala kecil, misalnya ke warung lokal atau tetangga."
)

@Composable
fun GoalPage(
    paddingValues: PaddingValues,
    selectedGoal: String,
    onGoalSelected: (String) -> Unit,
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
        Text("Tujuan & Skala", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = Icons.Default.TrackChanges,
            contentDescription = "Tujuan & Skala",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hasil panennya nanti untuk siapa?",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Opsi Pilihan Ganda
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SelectableOptionCard(
                title = "Konsumsi Sendiri",
                icon = Icons.Default.Home,
                isSelected = selectedGoal == goalOptions["Konsumsi Sendiri"],
                onClick = { onGoalSelected(goalOptions["Konsumsi Sendiri"]!!) }
            )
            SelectableOptionCard(
                title = "Berbagi dengan Tetangga",
                icon = Icons.Default.CardGiftcard,
                isSelected = selectedGoal == goalOptions["Berbagi dengan Tetangga"],
                onClick = { onGoalSelected(goalOptions["Berbagi dengan Tetangga"]!!) }
            )
            SelectableOptionCard(
                title = "Untuk Dijual Skala Kecil",
                icon = Icons.Default.Storefront,
                isSelected = selectedGoal == goalOptions["Untuk Dijual Skala Kecil"],
                onClick = { onGoalSelected(goalOptions["Untuk Dijual Skala Kecil"]!!) }
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
                enabled = selectedGoal.isNotBlank(), // Aktif jika sudah memilih
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))
            ) { Text("Selanjutnya") }
        }
    }
}

// Composable ini bisa dipindahkan ke file terpisah agar bisa di-reuse dengan lebih mudah
@Composable
private fun SelectableOptionCard(
    title: String,
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
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
    }
}