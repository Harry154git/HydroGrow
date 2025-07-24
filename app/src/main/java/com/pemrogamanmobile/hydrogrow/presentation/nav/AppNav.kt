package com.pemrogamanmobile.hydrogrow.presentation.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
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
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withnoai.MakeGardenScreen
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.makegarden.withai.MakeGardenAIScreen
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
                        navigateToMakeGardenInput = { navController.navigate("make_garden_manual") },
                        navigateToAIAssistedGarden = { navController.navigate("make_garden_input") },
                        navigateToGarden = { gardenId -> navController.navigate("garden_screen/$gardenId") }
                    )
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

                // --- Rute untuk ChatBotScreen ---
                composable(
                    route = "$chatbotRoute?chatbotId={chatbotId}",
                    arguments = listOf(navArgument("chatbotId") {
                        type = NavType.StringType
                        nullable = true
                    })
                ) { backStackEntry ->
                    val chatbotId = backStackEntry.arguments?.getString("chatbotId")
                    ChatBotScreen(
                        navController = navController,
                        chatbotId = chatbotId
                    )
                }

                // ✅ Rute baru untuk membuat kebun secara manual (tanpa AI)
                composable("make_garden_manual") {
                    MakeGardenScreen(navController = navController)
                }

                composable("edit_garden/{gardenId}") { backStackEntry ->
                    val gardenId = backStackEntry.arguments?.getString("gardenId") ?: ""
                    EditGardenScreen(gardenId = gardenId, navController = navController, viewModel = hiltViewModel())
                }
                composable("make_garden_input") {
                    MakeGardenAIScreen(navController = navController)
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