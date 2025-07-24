package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.onboardingpage.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemrogamanmobile.hydrogrow.R
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.onboardingpage.OnboardingScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingPage5(
    pagerState: PagerState,
    viewModel: OnboardingScreenViewModel
) {
    val scope = rememberCoroutineScope()
    val userData by viewModel.userData.collectAsState()
    var selectedOption by remember { mutableStateOf(userData.timeAvailable) }

    val options = listOf(
        "Kurang dari 1 jam",
        "2-5 jam",
        "Lebih dari 5 jam"
    )

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
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notification_logo),
                    contentDescription = "Logo",
                    tint = Color(0xFF00684A),
                    modifier = Modifier.size(35.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "HydroGrow",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00684A)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Konten utama
            Text(
                text = "Berapa banyak waktu luang untuk berkebun per minggu?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF1B1C1A)
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Pilihan
            options.forEach { optionText ->
                val isSelected = selectedOption == optionText
                val borderColor = if (isSelected) Color(0xFF556200) else Color.White
                val containerColor = if (isSelected) Color(0xFFF0F5E1) else Color.White

                Card(
                    onClick = {
                        selectedOption = optionText
                        viewModel.onEvent(OnboardingScreenViewModel.OnboardingEvent.UpdateTimeAvailable(optionText))
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(2.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 20.dp) // Penyesuaian padding
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center // Teks di tengah
                    ) {
                        Text(
                            text = optionText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tombol Navigasi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF556200).copy(alpha = 0.8f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(text = "Sebelumnya")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    enabled = selectedOption.isNotEmpty(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF556200),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text(text = "Selanjutnya")
                }
            }
        }
    }
}