package com.pemrogamanmobile.hydrogrow.loginorregisterpage


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemrogamanmobile.hydrogrow.R
import com.pemrogamanmobile.hydrogrow.data.DataSource

@Composable
fun LoginScreen( onLoginSuccess: () -> Unit,
                 onRegisterClick: () -> Unit) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFF4CAF50), shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login",
                    fontSize = 24.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "Profile",
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val dataSource = DataSource.getInstance(context)
                        if (dataSource.checkUser(email.text, password.text)) {
                            Toast.makeText(
                                context,
                                "Login berhasil!",
                                Toast.LENGTH_SHORT
                            ).show()
                            onLoginSuccess()
                        } else {
                            Toast.makeText(
                                context,
                                "Kesalahan email atau password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Login")
                }


                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Belum punya akun? Registrasi",
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        onRegisterClick()
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
