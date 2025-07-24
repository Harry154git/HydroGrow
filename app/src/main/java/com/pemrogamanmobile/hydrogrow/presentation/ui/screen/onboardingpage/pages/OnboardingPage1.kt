package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.onboardingpage.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemrogamanmobile.hydrogrow.R // Pastikan Anda sudah menambahkan gambar ke drawable
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingPage1(pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFFEAF2E2), Color(0xFFD8E4C7))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notification_logo), // Ganti dengan logo Anda
                    contentDescription = "Logo",
                    tint = Color(0xFF00684A),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "HydroGrow",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00684A)
                )
            }

            // Konten Tengah
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Bergabung dengan Revolusi Berkebun Modern",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF1B1C1A),
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_notification_logo), // Ganti dengan gambar ilustrasi Anda
                    contentDescription = "Ilustrasi Berkebun",
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Tanam, bagikan inspirasi, dan terhubung dengan ribuan pehobi hidroponik seperti Anda",
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    fontSize = 16.sp
                )
            }

            // Tombol
            Button(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF556200)
                ),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp)
            ) {
                Text(text = "Lanjut", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}