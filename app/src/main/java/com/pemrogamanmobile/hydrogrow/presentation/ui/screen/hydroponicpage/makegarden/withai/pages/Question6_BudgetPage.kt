package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withai.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // <-- IMPORT INI
import androidx.compose.foundation.verticalScroll // <-- IMPORT INI
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val budgetOptions = mapOf(
    "< Rp 300.000" to "Anggaran yang disiapkan sangat terbatas, di bawah Rp 300.000.",
    "Rp 300.000 - Rp 1.000.000" to "Anggaran yang disiapkan berada di rentang Rp 300.000 hingga Rp 1.000.000.",
    "Rp 1.000.000 - Rp 2.500.000" to "Anggaran yang disiapkan berada di rentang Rp 1.000.000 hingga Rp 2.500.000.",
    "> Rp 2.500.000" to "Anggaran yang disiapkan cukup fleksibel, di atas Rp 2.500.000."
)

@Composable
fun BudgetPage(
    paddingValues: PaddingValues,
    selectedBudget: String,
    onBudgetSelected: (String) -> Unit,
    onFinish: () -> Unit,
    onPrevious: () -> Unit
) {
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
            Text("Biaya", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = "Biaya",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Terakhir, berapa kira-kira anggaran yang kamu siapkan?",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Opsi Pilihan Ganda
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                budgetOptions.forEach { (uiText, promptText) ->
                    SelectableOptionCard(
                        title = uiText,
                        isSelected = selectedBudget == promptText,
                        onClick = { onBudgetSelected(promptText) }
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
            Button(
                onClick = onPrevious,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8F9779))
            ) { Text("Sebelumnya") }

            Button(
                onClick = onFinish,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedBudget.isNotBlank(), // Aktif jika sudah memilih
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B8E23))
            ) { Text("Selesai") }
        }
    }
}

@Composable
private fun SelectableOptionCard(
    title: String,
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
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
    }
}