package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.profilpage

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.profilpage.ProfileViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import com.pemrogamanmobile.hydrogrow.R


@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogoutSuccess: () -> Unit,
    onBack: () -> Unit
) {
//    val state by viewModel.state.collectAsState()
//    val context = LocalContext.current
//
//    var name by rememberSaveable { mutableStateOf("") }
//    var email by rememberSaveable { mutableStateOf("") }
//    var phone by rememberSaveable { mutableStateOf("") }
//    var address by rememberSaveable { mutableStateOf("") }
//    var password by rememberSaveable { mutableStateOf("") }
//    var isInitialized by rememberSaveable { mutableStateOf(false) }
//
//    LaunchedEffect(state.user) {
//        val user = state.user
//        if (user != null && !isInitialized) {
//            name = user.name
//            email = user.email
//            phone = user.phone
//            address = user.address
//            password = user.password
//            isInitialized = true
//        }
//    }
//
//    if (state.isLoading) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }
//    } else {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//                .verticalScroll(rememberScrollState()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.user),
//                contentDescription = "Profile Photo",
//                modifier = Modifier.size(120.dp),
//                alignment = Alignment.Center
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            val greenColor = Color(0xFF4CAF50)
//
//            OutlinedTextField(
//                value = name,
//                onValueChange = { name = it },
//                label = { Text("Nama") },
//                modifier = Modifier.fillMaxWidth(),
//                colors = TextFieldDefaults.colors(
//                    focusedIndicatorColor = greenColor,
//                    unfocusedIndicatorColor = greenColor,
//                    disabledIndicatorColor = greenColor,
//                    cursorColor = greenColor
//                )
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = email,
//                onValueChange = {},
//                label = { Text("Email") },
//                enabled = false,
//                modifier = Modifier.fillMaxWidth(),
//                colors = TextFieldDefaults.colors(
//                    disabledIndicatorColor = greenColor,
//                    disabledTextColor = Color.Gray,
//                    disabledLabelColor = Color.Gray
//                )
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = phone,
//                onValueChange = { phone = it },
//                label = { Text("Nomor Telepon") },
//                modifier = Modifier.fillMaxWidth(),
//                colors = TextFieldDefaults.colors(
//                    focusedIndicatorColor = greenColor,
//                    unfocusedIndicatorColor = greenColor,
//                    cursorColor = greenColor
//                )
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = address,
//                onValueChange = { address = it },
//                label = { Text("Alamat") },
//                modifier = Modifier.fillMaxWidth(),
//                colors = TextFieldDefaults.colors(
//                    focusedIndicatorColor = greenColor,
//                    unfocusedIndicatorColor = greenColor,
//                    cursorColor = greenColor
//                )
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text("Password (baru)") },
//                modifier = Modifier.fillMaxWidth(),
//                visualTransformation = PasswordVisualTransformation(),
//                colors = TextFieldDefaults.colors(
//                    focusedIndicatorColor = greenColor,
//                    unfocusedIndicatorColor = greenColor,
//                    cursorColor = greenColor
//                )
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(
//                onClick = {
//                    val user = state.user
//                    if (user != null) {
//                        val updatedUser = user.copy(
//                            name = name,
//                            phone = phone,
//                            address = address,
//                            password = password
//                        )
//                        viewModel.updateProfile(updatedUser)
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                colors = ButtonDefaults.buttonColors(containerColor = greenColor)
//            ) {
//                Text("Simpan Perubahan")
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//
//            OutlinedButton(
//                onClick = onBack,
//                modifier = Modifier.fillMaxWidth(),
//                border = BorderStroke(1.dp, greenColor),
//                colors = ButtonDefaults.outlinedButtonColors(contentColor = greenColor)
//            ) {
//                Text("Kembali")
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//
//            OutlinedButton(
//                onClick = {
//                    viewModel.logout()
//                    onLogoutSuccess()
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Logout")
//            }
//        }
//    }
//
//    state.error?.let {
//        LaunchedEffect(it) {
//            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
//        }
//    }
}
