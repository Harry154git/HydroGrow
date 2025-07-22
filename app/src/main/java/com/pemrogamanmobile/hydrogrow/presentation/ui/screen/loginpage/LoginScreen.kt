package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.loginpage

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.pemrogamanmobile.hydrogrow.R // Pastikan import R benar
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.loginpage.LoginState
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.loginpage.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val state = viewModel.uiState

    // Konfigurasi Google Sign-In
    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // GANTI DENGAN WEB CLIENT ID DARI file google-services.json ANDA
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, googleSignInOptions) }

    // Launcher untuk menangani hasil dari intent Google Sign-In
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                viewModel.signInWithGoogle(credential) // Kirim kredensial ke ViewModel
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign In gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Efek untuk menangani navigasi atau pesan error berdasarkan state
    LaunchedEffect(key1 = state) {
        when (state) {
            is LoginState.Success -> {
                Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                onLoginSuccess() // Panggil callback untuk navigasi
                viewModel.resetState() // Reset state setelah navigasi
            }
            is LoginState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                viewModel.resetState() // Reset state setelah menampilkan error
            }
            else -> Unit // Idle atau Loading ditangani di UI di bawah
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB8D4E3), // Light blue
                        Color(0xFFE8F5B8)  // Light green
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Section (diubah sesuai permintaan)
            Image(
                painter = painterResource(id = R.drawable.ic_notification_logo),
                contentDescription = "Hydrogrow Logo",
                modifier = Modifier.size(120.dp) // Sesuaikan ukuran jika perlu
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = "Hydrogrow",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A5D23), // Dark green
                textAlign = TextAlign.Center
            )
        }

        // Google Sign In Button di bawah
        GoogleSignInButton(
            // Nonaktifkan tombol saat sedang loading
            enabled = state !is LoginState.Loading,
            onClick = {
                // Jalankan intent Google Sign-In
                launcher.launch(googleSignInClient.signInIntent)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 48.dp)
        )

        // Tampilkan loading indicator di tengah layar jika state adalah Loading
        if (state is LoginState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true // Tambahkan parameter enabled
) {
    Button(
        onClick = onClick,
        enabled = enabled, // Gunakan parameter enabled
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.LightGray // Warna saat nonaktif
        ),
        shape = RoundedCornerShape(28.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // ... (Isi Row tetap sama)
            // ...
        }
    }
}