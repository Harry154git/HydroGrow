@file:Suppress("UNREACHABLE_CODE")

package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.onboardingpage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.Colors.buttonGreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.Colors.cardBackgroundColor
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.Colors.lightGreenBgEnd
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.Colors.lightGreenBgStart
import com.pemrogamanmobile.hydrogrow.presentation.ui.components.Colors.textDarkGreen
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.onboardingpage.OnboardingScreenViewModel

@Composable
fun OnBoardingScreen(
    // 1. Tambahkan callback untuk navigasi
    onNavigateToLogin: () -> Unit,
    // 2. Inject ViewModel menggunakan Hilt
    viewModel: OnboardingScreenViewModel = hiltViewModel()
) {
    // State to keep track of the selected option
    var selectedOption by remember { mutableStateOf<String?>(null) }

    // 3. Gunakan LaunchedEffect untuk mengamati event dari ViewModel
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is OnboardingScreenViewModel.UiEvent.NavigateToLogin -> {
                    onNavigateToLogin()
                }
            }
        }
    }

    // Vertical gradient for the background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(lightGreenBgStart, lightGreenBgEnd)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogo()
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "Seberapa berpengalaman Anda dengan Hidroponik?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textDarkGreen,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            ExperienceOption(
                text = "Baru mau coba",
                icon = Icons.Default.Spa,
                isSelected = selectedOption == "Baru mau coba",
                onClick = { selectedOption = "Baru mau coba" }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ExperienceOption(
                text = "Sudah Pernah",
                icon = Icons.Default.Eco,
                isSelected = selectedOption == "Sudah Pernah",
                onClick = { selectedOption = "Sudah Pernah" }
            )
            Spacer(modifier = Modifier.height(16.dp))
            ExperienceOption(
                text = "Sudah ahli",
                icon = Icons.Default.EmojiEvents,
                isSelected = selectedOption == "Sudah ahli",
                onClick = { selectedOption = "Sudah ahli" }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 4. Panggil event ViewModel saat tombol ditekan
                NavButton(
                    text = "Skip",
                    onClick = { viewModel.onEvent(OnboardingScreenViewModel.OnboardingEvent.SaveAndNavigate) }
                )
                NavButton(
                    text = "Selanjutnya",
                    onClick = { viewModel.onEvent(OnboardingScreenViewModel.OnboardingEvent.SaveAndNavigate) }
                )
            }
        }
    }
}


@Composable
fun NavButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50), // Fully rounded corners
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonGreen,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        modifier = Modifier.width(140.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun ExperienceOption(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val elevation = if (isSelected) 8.dp else 4.dp
    val border = if (isSelected) BorderStroke(2.dp, buttonGreen) else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = border
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = textDarkGreen,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = textDarkGreen
            )
        }
    }
}

@Composable
fun AppLogo() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = "HydroGrow Logo",
            tint = textDarkGreen,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "HydroGrow",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = textDarkGreen
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    OnBoardingScreen(
        onNavigateToLogin = TODO(),
        viewModel = TODO()
    )
}
