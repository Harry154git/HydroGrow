package com.pemrogamanmobile.hydrogrow.onboardingpage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnBoardingScreen(onSkip: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Button(
            onClick = { onSkip() }
        ) {
            Text(
                text = "Lewati",
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
