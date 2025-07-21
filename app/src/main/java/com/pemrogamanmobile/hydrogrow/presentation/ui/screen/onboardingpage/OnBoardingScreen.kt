package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.onboardingpage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.onboardingpage.TampilanViewModel

@Composable
fun OnBoardingScreen(
    viewModel: TampilanViewModel = hiltViewModel(),
    onSkip: () -> Unit,
    onLogin: () -> Unit
) {
    val tipsImages = viewModel.tipsImages
    var currentPage by remember { mutableStateOf(0) }
    val lastPage = tipsImages.size - 1

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = tipsImages[currentPage]),
            contentDescription = "Onboarding Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        )


        Button(
            onClick = { onSkip() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text("Lewati")
        }

        Button(
            onClick = {
                if (currentPage == lastPage) {
                    onLogin()
                } else {
                    currentPage++
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(if (currentPage == lastPage) "Login" else "Next")
        }
    }
}

