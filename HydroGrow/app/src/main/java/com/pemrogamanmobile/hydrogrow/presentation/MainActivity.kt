package com.pemrogamanmobile.hydrogrow.presentation

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import com.pemrogamanmobile.hydrogrow.presentation.nav.AppNav
import com.pemrogamanmobile.hydrogrow.presentation.ui.theme.HydroGrowTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 1. Buat launcher untuk meminta izin
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Izin diberikan. Kita bisa melakukan sesuatu jika perlu.
        } else {
            // Izin ditolak. Beri tahu pengguna bahwa fitur notifikasi tidak akan berfungsi.
        }
    }

    private fun askNotificationPermission() {
        // Hanya berlaku untuk Android 13 (Tiramisu) atau lebih baru
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 2. Cek apakah izin sudah diberikan
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Izin sudah ada, tidak perlu melakukan apa-apa.
            } else {
                // 3. Jika belum, minta izin kepada pengguna
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        askNotificationPermission()
        setContent {
            HydroGrowTheme {
                AppNav()
            }
        }

    }
}

