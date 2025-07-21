package com.pemrogamanmobile.hydrogrow.presentation.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.topbar.TopBarApp
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.loginorregisterpage.LoginScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.loginorregisterpage.RegisterScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.onboardingpage.OnBoardingScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.home.HomeScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.profilpage.ProfileScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.GardenScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage.PlantScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage.EditScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.MakeGardenInput
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.MakeGardenOutput
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.EditGardenScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage.AddPlantScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.settingspage.SettingsScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.chatbotpage.ChatBotScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.onboardingpage.OnboardingScreenViewModel
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.profilpage.ProfileViewModel

@Composable
fun AppNav() {
    val navController: NavHostController = rememberNavController()
    val onboardingViewModel: OnboardingScreenViewModel = hiltViewModel()
    val onboardingCompleted = onboardingViewModel.onboardingCompleted.collectAsStateWithLifecycle()

    if (onboardingCompleted.value == null) {
        // Tampilkan loading indikator saat status onboarding masih belum didapat
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator()
        }
    } else {
        // Tentukan startDestination
        val startDestination = if (onboardingCompleted.value == true) "login" else "onboarding"

        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable("onboarding") {
                OnBoardingScreen(
                    onSkip = {
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    },
                    onLogin = {
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            composable("login") {
                WithTopBar(navController) {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onNavigateToRegister = {
                            navController.navigate("register")
                        }
                    )
                }
            }

            composable("register") {
                WithTopBar(navController) {
                    RegisterScreen(
                        onRegisterSuccess = {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable("home") {
                WithTopBar(navController) {
                    HomeScreen(
                        navigateToProfile = {
                            navController.navigate("profile_screen")
                        },
                        navigateToMakeGardenInput = {
                            navController.navigate("make_Garden_input")
                        },
                        navigateToGarden = { gardenId ->
                            navController.navigate("garden_screen/$gardenId")
                        },
                        navigateToPlant = { plantId ->
                            navController.navigate("plant_screen/$plantId")
                        },
                        navigateToAddPlant = {
                            navController.navigate("add_plant")
                        }
                    )
                }
            }

            composable("profile_screen") {
                WithTopBar(navController) {
                    val viewModel: ProfileViewModel = hiltViewModel()
                    ProfileScreen(
                        viewModel = viewModel,
                        onLogoutSuccess = {
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            composable("edit_garden/{gardenId}") { backStackEntry ->
                val gardenId = backStackEntry.arguments?.getString("gardenId") ?: ""
                WithTopBar(navController) {
                    EditGardenScreen(
                        gardenId = gardenId,
                        navController = navController,
                        viewModel = hiltViewModel()
                    )
                }
            }

            composable("make_garden_input") {
                WithTopBar(navController) {
                    MakeGardenInput(
                        onNext = { navController.navigate("make_garden_output") }
                    )
                }
            }

            composable("make_garden_output") {
                WithTopBar(navController) {
                    MakeGardenOutput(
                        onBackToHome = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable("garden_screen/{gardenId}") { backStackEntry ->
                val gardenId = backStackEntry.arguments?.getString("gardenId") ?: ""
                WithTopBar(navController) {
                    GardenScreen(
                        gardenId = gardenId,
                        viewModel = hiltViewModel(),
                        navController = navController
                    )
                }
            }

            composable("add_plant") {
                WithTopBar(navController) {
                    AddPlantScreen(
                        onBack = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable("plant_screen/{plantId}") {backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
                WithTopBar(navController) {
                    PlantScreen(
                        plantId = plantId,
                        viewModel = hiltViewModel(),
                        navController = navController
                    )
                }
            }

            composable("edit_plant/{plantId}") {backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
                WithTopBar(navController) {
                    EditScreen(
                        plantId = plantId,
                        navController = navController
                    )
                }
            }

            composable("chatbot") {
                WithTopBar(navController) {
                    ChatBotScreen(navController)
                }
            }

            composable("settings") {
                WithTopBar(navController) {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun WithTopBar(navController: NavHostController, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopBarApp(navController = navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            content()
        }
    }
}
