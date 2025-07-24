package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withai

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withai.pages.*
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.hydroponicpage.makegarden.withai.MakeGardenAiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeGardenAIScreen(
    navController: NavController,
    viewModel: MakeGardenAiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Menangani side-effect seperti navigasi atau menampilkan Toast
    LaunchedEffect(key1 = uiState.isSaveSuccess, key2 = uiState.error) {
        if (uiState.isSaveSuccess) {
            Toast.makeText(context, "Kebun baru berhasil disimpan!", Toast.LENGTH_SHORT).show()
            // Kembali ke halaman utama setelah sukses
            navController.popBackStack(navController.graph.startDestinationId, false)
        }
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
            viewModel.resetError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                // Jangan tampilkan TopBar di halaman intro (page 0)
                if (uiState.currentPage > 0) {
                    TopAppBar(
                        title = { Text("") },
                        navigationIcon = {
                            IconButton(onClick = { viewModel.previousPage() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Sebelumnya"
                                )
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            Crossfade(
                targetState = uiState.currentPage,
                label = "page_transition"
            ) { page ->
                when (page) {
                    0 -> IntroPage(
                        paddingValues = paddingValues,
                        userName = uiState.userName,
                        onStart = { viewModel.nextPage() }
                    )
                    1 -> LightConditionPage(
                        paddingValues = paddingValues,
                        selectedCondition = uiState.kondisiCahaya,
                        onConditionSelected = { viewModel.onStateChange(uiState.copy(kondisiCahaya = it)) },
                        onNext = { viewModel.nextPage() },
                        onPrevious = { viewModel.previousPage() }
                    )
                    2 -> LandSizePage(
                        paddingValues = paddingValues,
                        panjang = uiState.panjangLahan,
                        lebar = uiState.lebarLahan,
                        isRecommendationChecked = uiState.mintaRekomendasiLahan,
                        onPanjangChange = { viewModel.onStateChange(uiState.copy(panjangLahan = it)) },
                        onLebarChange = { viewModel.onStateChange(uiState.copy(lebarLahan = it)) },
                        onRecommendationToggle = { viewModel.onStateChange(uiState.copy(mintaRekomendasiLahan = it)) },
                        onNext = { viewModel.nextPage() },
                        onPrevious = { viewModel.previousPage() }
                    )
                    3 -> TemperaturePage(
                        paddingValues = paddingValues,
                        selectedTemperature = uiState.suhuCuaca,
                        onTemperatureSelected = { viewModel.onStateChange(uiState.copy(suhuCuaca = it)) },
                        onNext = { viewModel.nextPage() },
                        onPrevious = { viewModel.previousPage() }
                    )
                    4 -> PlantTypePage(
                        paddingValues = paddingValues,
                        selectedPlantType = uiState.jenisTanaman,
                        isRecommendationChecked = uiState.mintaRekomendasiTanaman,
                        onPlantTypeSelected = { viewModel.onStateChange(uiState.copy(jenisTanaman = it, mintaRekomendasiTanaman = false)) },
                        onRecommendationToggle = { viewModel.onStateChange(uiState.copy(mintaRekomendasiTanaman = it, jenisTanaman = "")) },
                        onNext = { viewModel.nextPage() },
                        onPrevious = { viewModel.previousPage() }
                    )
                    5 -> GoalPage(
                        paddingValues = paddingValues,
                        selectedGoal = uiState.tujuanSkala,
                        onGoalSelected = { viewModel.onStateChange(uiState.copy(tujuanSkala = it)) },
                        onNext = { viewModel.nextPage() },
                        onPrevious = { viewModel.previousPage() }
                    )
                    6 -> BudgetPage(
                        paddingValues = paddingValues,
                        selectedBudget = uiState.rentangBiaya,
                        onBudgetSelected = { viewModel.onStateChange(uiState.copy(rentangBiaya = it)) },
                        onFinish = { viewModel.nextPage() },
                        onPrevious = { viewModel.previousPage() }
                    )
                    7 -> uiState.aiPlan?.let { plan ->
                        ResultPage(
                            paddingValues = paddingValues,
                            plan = plan,
                            onSave = { viewModel.saveGardenPlan() }
                        )
                    }
                }
            }
        }
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}