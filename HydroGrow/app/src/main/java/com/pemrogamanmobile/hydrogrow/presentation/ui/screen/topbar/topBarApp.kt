package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.topbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.pemrogamanmobile.hydrogrow.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarApp(navController: NavController) {
    Surface(
        color = Color(0xFF4CAF50),
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(0.dp)
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(
                    text = "",
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.setting),
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            },
            actions = {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Company Logo",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 8.dp)
                        .align(Alignment.CenterVertically)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF4CAF50),
                scrolledContainerColor = Color(0xFF388E3C),
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        )
    }
}