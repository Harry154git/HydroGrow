package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.bottomnavigationbar

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pemrogamanmobile.hydrogrow.presentation.nav.BottomNavItem

@Composable
fun BottomNavigationBar(navController: NavController) {
    // Daftar item yang akan ditampilkan
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Chatbot,
        BottomNavItem.Profil,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        // 2. Mengubah warna background seluruh bottom navigation bar
        containerColor = Color(0xFFFFFFFF), // Ganti dengan warna yang Anda inginkan
        modifier = Modifier
    ) {
        items.forEach { item ->
            NavigationBarItem(
                // 1. Membuat setiap item memiliki lebar yang sama dan mengisi ruang
                modifier = Modifier.weight(1f),

                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.size(28.dp) // Ukuran ikon bisa disesuaikan
                    )
                },
                // 3. Mengubah warna saat item dipilih
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    // Ini adalah warna background (indikator) saat item dipilih
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}