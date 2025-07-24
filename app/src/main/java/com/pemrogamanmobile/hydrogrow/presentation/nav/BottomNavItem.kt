package com.pemrogamanmobile.hydrogrow.presentation.nav

import com.pemrogamanmobile.hydrogrow.R

/**
 * Sealed class untuk mendefinisikan item-item di Bottom Navigation Bar.
 *
 * @param route Alamat navigasi unik untuk setiap screen.
 * @param icon Resource ID untuk ikon dari drawable.
 * @param title Judul yang akan ditampilkan di bawah ikon.
 */
sealed class BottomNavItem(val route: String, val icon: Int, val title: String) {
    object Home : BottomNavItem("home", R.drawable.ic_home, "Home")
    object Chatbot : BottomNavItem("chatbot_screen", R.drawable.ic_chatbot, "Chatbot")
    object Profil : BottomNavItem("info", R.drawable.ic_profile_info, "Info Profil")
}