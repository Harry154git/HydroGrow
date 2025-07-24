package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.onboardingpage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.onboardingpage.pages.*
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.onboardingpage.OnboardingScreenViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingScreenViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 7 })

    // Listener untuk event navigasi dari ViewModel
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is OnboardingScreenViewModel.UiEvent.NavigateToHome -> {
                    onOnboardingComplete() // Panggil callback saat event diterima
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> OnboardingPage1(pagerState = pagerState)
                1 -> OnboardingPage2(pagerState = pagerState)
                2 -> OnboardingPage3(pagerState = pagerState)
                3 -> OnboardingPage4(pagerState = pagerState, viewModel = viewModel)
                4 -> OnboardingPage5(pagerState = pagerState, viewModel = viewModel)
                5 -> OnboardingPage6(pagerState = pagerState, viewModel = viewModel)
                6 -> OnboardingPage7(viewModel = viewModel)
            }
        }
    }
}