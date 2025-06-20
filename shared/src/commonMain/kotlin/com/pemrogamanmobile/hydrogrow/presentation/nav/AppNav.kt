package com.pemrogamanmobile.hydrogrow.presentation.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pemrogamanmobile.hydrogrow.topbar.TopBarApp
import com.pemrogamanmobile.hydrogrow.loginorregisterpage.LoginScreen
import com.pemrogamanmobile.hydrogrow.onboardingpage.OnBoardingScreen
import com.pemrogamanmobile.hydrogrow.home.HomeScreen
import com.pemrogamanmobile.hydrogrow.profilpage.ProfilScreen
import com.pemrogamanmobile.hydrogrow.hydroponicpage.HydroponicScreen
import com.pemrogamanmobile.hydrogrow.newspage.NewsScreen
import com.pemrogamanmobile.hydrogrow.plantpage.PlantScreen
import com.pemrogamanmobile.hydrogrow.loginorregisterpage.RegisterScreen
import com.pemrogamanmobile.hydrogrow.hydroponicpage.MakeHydroponic
import com.pemrogamanmobile.hydrogrow.hydroponicpage.MakeHydroponic2
import com.pemrogamanmobile.hydrogrow.hydroponicpage.EditScreen
import com.pemrogamanmobile.hydrogrow.hydroponicpage.HydroponicResult
import com.pemrogamanmobile.hydrogrow.hydroponicpage.AddPlantScreen
import com.pemrogamanmobile.hydrogrow.settingspage.SettingsScreen
import com.pemrogamanmobile.hydrogrow.plantpage.EditScreen



@Composable
fun AppNav() {
    val navController: NavHostController = rememberNavController()

    Scaffold(
        topBar = {
            TopBarApp(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "onboarding",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("onboarding") {
                OnBoardingScreen(onSkip = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                })
            }

            composable("login") {
                LoginScreen(
                    onLoginSuccess = { navController.navigate("homes") },
                    onRegisterClick = { navController.navigate("register") }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = { navController.navigate("login") }
                )
            }

            composable("homes") {
                HomeScreen(
                    navigateToProfile = { navController.navigate("profile") },
                    navigateToMakeHydroponic = { navController.navigate("make_hydroponic") },
                    navigateToHydroponic = { gardenName, _ ->
                        navController.navigate("hydroponic/$gardenName")
                    },
                    navigateToPlant = { plantName, _ ->
                        navController.navigate("plant/$plantName")
                    },
                    navigateToNews = { navController.navigate("news") },
                    navigateToMakePlant = { navController.navigate("add_plant") }
                )
            }

            composable("profile") {
                ProfilScreen(
                    onBackToHome = { navController.navigate("homes") }
                )
            }

            composable("edit/{kebunId}") { backStackEntry ->
                val kebunId = backStackEntry.arguments?.getString("kebunId")?.toIntOrNull() ?: return@composable
                EditScreen(kebunId = kebunId, navController = navController)
            }


            composable("make_hydroponic") {
                MakeHydroponic { result ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("hydroponic_result", result)
                    navController.navigate("make_hydroponic2")
                }
            }

            composable("make_hydroponic2") {
                val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
                val result = savedStateHandle?.get<HydroponicResult>("hydroponic_result")

                if (result != null) {
                    MakeHydroponic2(
                        result = result,
                        onSave = { hydroponicGarden ->
                            println("Disimpan: $hydroponicGarden")
                        },
                        onBackToHome = { navController.navigate("homes") }
                    )
                } else {
                    Text("Data tidak tersedia.")
                }
            }


            composable("hydroponic/{gardenName}") { backStackEntry ->
                val gardenName = backStackEntry.arguments?.getString("gardenName") ?: ""
                HydroponicScreen(navController, gardenName)
            }

            composable("add_plant") {
                AddPlantScreen(
                    onBack = { navController.navigate("homes") }
                )
            }

            composable("plant/{plantName}") { backStackEntry ->
                val plantName = backStackEntry.arguments?.getString("plantName") ?: ""
                PlantScreen(navController,plantName = plantName)
            }

            composable("edit/{plantName}") { backStackEntry ->
                val plantName = backStackEntry.arguments?.getString("plantName") ?: ""
                EditScreen(navController = navController, plantName = plantName)
            }


            composable("news") {
                NewsScreen()
            }

            composable("settings") {
                SettingsScreen()
            }


        }
    }
}
