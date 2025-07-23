package com.pemrogamanmobile.hydrogrow.presentation.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.chatbotpage.ChatBotScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.bottomnavigationbar.BottomNavigationBar
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.home.HomeScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.*
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.loginpage.LoginScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.onboardingpage.OnBoardingScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage.AddPlantScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage.EditScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage.PlantScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.profilpage.ProfileScreen
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.profilpage.ProfileViewModel

@Composable
fun AppNav() {
    val navController: NavHostController = rememberNavController()
    val appNavViewModel: AppNavViewModel = hiltViewModel()
    val appState = appNavViewModel.appState.collectAsStateWithLifecycle()

    // Definisikan rute chatbot yang baru di sini agar konsisten
    val chatbotRoute = "chatbot_screen"

    if (appState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val startDestination = when {
            appState.value.isSignedIn -> BottomNavItem.Home.route
            appState.value.isOnboardingCompleted -> "login"
            else -> "onboarding"
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val screensWithoutBar = listOf("onboarding", "login")
        val showBottomBar = currentRoute !in screensWithoutBar

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(navController = navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                // --- Rute tanpa Bottom Bar ---
                composable("onboarding") {
                    OnBoardingScreen(onNavigateToLogin = {
                        navController.navigate("login") { popUpTo("onboarding") { inclusive = true } }
                    })
                }
                composable("login") {
                    LoginScreen(onLoginSuccess = {
                        navController.navigate(BottomNavItem.Home.route) { popUpTo("login") { inclusive = true } }
                    })
                }

                // --- Rute Utama dengan Bottom Bar ---
                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        navigateToMakeGardenInput = { navController.navigate("make_garden_input") },
                        navigateToGarden = { gardenId -> navController.navigate("garden_screen/$gardenId") },
                        navigateToPlant = { plantId -> navController.navigate("plant_screen/$plantId") },
                        navigateToAddPlant = { navController.navigate("add_plant") }
                    )
                }

                composable(BottomNavItem.Community.route) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Halaman Komunitas")
                    }
                }

                composable(BottomNavItem.Postings.route) {
                    // Konten untuk Postings, jika ada, diletakkan di sini
                }

                composable(BottomNavItem.Profil.route) {
                    val viewModel: ProfileViewModel = hiltViewModel()
                    ProfileScreen(
                        viewModel = viewModel,
                        onLogoutSuccess = {
                            navController.navigate("login") { popUpTo(BottomNavItem.Home.route) { inclusive = true } }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                // --- Rute untuk ChatBotScreen yang bisa menerima ID ---
                composable(
                    route = "$chatbotRoute?chatbotId={chatbotId}",
                    arguments = listOf(navArgument("chatbotId") {
                        type = NavType.StringType
                        nullable = true // Penting! Agar argumen bersifat opsional
                    })
                ) { backStackEntry ->
                    val chatbotId = backStackEntry.arguments?.getString("chatbotId")
                    ChatBotScreen(
                        navController = navController,
                        chatbotId = chatbotId // Teruskan ID ke screen
                    )
                }

                // --- Rute Lainnya (yang tetap menampilkan Bottom Bar) ---
                composable("edit_garden/{gardenId}") { backStackEntry ->
                    val gardenId = backStackEntry.arguments?.getString("gardenId") ?: ""
                    EditGardenScreen(gardenId = gardenId, navController = navController, viewModel = hiltViewModel())
                }
                composable("make_garden_input") {
                    MakeGardenInput(onNext = { navController.navigate("make_garden_output") })
                }
                composable("make_garden_output") {
                    MakeGardenOutput(onBackToHome = {
                        navController.navigate(BottomNavItem.Home.route) { popUpTo(BottomNavItem.Home.route) { inclusive = true } }
                    })
                }
                composable("garden_screen/{gardenId}") { backStackEntry ->
                    val gardenId = backStackEntry.arguments?.getString("gardenId") ?: ""
                    GardenScreen(gardenId = gardenId, viewModel = hiltViewModel(), navController = navController)
                }
                composable("add_plant") {
                    AddPlantScreen(onBack = {
                        navController.navigate(BottomNavItem.Home.route) { popUpTo(BottomNavItem.Home.route) { inclusive = true } }
                    })
                }
                composable("plant_screen/{plantId}") { backStackEntry ->
                    val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
                    PlantScreen(plantId = plantId, viewModel = hiltViewModel(), navController = navController)
                }
                composable("edit_plant/{plantId}") { backStackEntry ->
                    val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
                    EditScreen(plantId = plantId, navController = navController)
                }
            }
        }
    }
}