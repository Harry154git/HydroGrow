package com.pemrogamanmobile.hydrogrow.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import com.pemrogamanmobile.hydrogrow.presentation.nav.AppNav
import com.pemrogamanmobile.hydrogrow.presentation.ui.theme.HydroGrowTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HydroGrowTheme {
                AppNav()
            }
        }

    }
}

