package com.pemrogamanmobile.hydrogrow.presentation.ui.screen.plantpage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pemrogamanmobile.hydrogrow.R
import com.pemrogamanmobile.hydrogrow.presentation.ui.screen.hydroponicpage.InfoRow
import com.pemrogamanmobile.hydrogrow.presentation.viewmodel.plantpage.EditPlantViewModel

@Composable
fun EditScreen(
    viewModel: EditPlantViewModel = hiltViewModel(),
    navController: NavController,
    plantId: String
) {
//    val state = viewModel.uiState
//    var isEditing by rememberSaveable { mutableStateOf(false) }
//
//    // Local fields for edit
//    var localPlantName by rememberSaveable { mutableStateOf("") }
//    var localNutrients by rememberSaveable { mutableStateOf("") }
//    var localHarvestTime by rememberSaveable { mutableStateOf("") }
//    var isInitialized by rememberSaveable { mutableStateOf(false) }
//
//    LaunchedEffect(plantId) {
//        viewModel.loadPlant(plantId)
//    }
//
//    if (!isInitialized && !state.isLoading) {
//        localPlantName = state.plantName
//        localNutrients = state.nutrientsUsed
//        localHarvestTime = state.harvestTime
//        isInitialized = true
//    }
//
//    if (state.isSuccess) {
//        LaunchedEffect(true) {
//            navController.popBackStack()
//        }
//    }
//
//    if (state.isLoading) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator()
//        }
//        return
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(16.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text("Edit Tanaman", fontSize = 24.sp, fontWeight = FontWeight.Bold)
//            Button(onClick = { navController.popBackStack() }) {
//                Text("Kembali")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Image(
//            painter = painterResource(id = R.drawable.logo), // placeholder
//            contentDescription = "Tanaman",
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        InfoRow(
//            "Nama Tanaman:",
//            if (isEditing) localPlantName else state.plantName,
//            isEditing
//        ) { localPlantName = it }
//
//        InfoRow(
//            "Nutrisi:",
//            if (isEditing) localNutrients else state.nutrientsUsed,
//            isEditing
//        ) { localNutrients = it }
//
//        InfoRow(
//            "Masa Panen:",
//            if (isEditing) localHarvestTime else state.harvestTime,
//            isEditing
//        ) { localHarvestTime = it }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Button(onClick = {
//                if (!isEditing) {
//                    // Saat masuk ke mode edit, ambil data dari state
//                    localPlantName = state.plantName
//                    localNutrients = state.nutrientsUsed
//                    localHarvestTime = state.harvestTime
//                }
//                isEditing = !isEditing
//            }) {
//                Text(if (isEditing) "Batal" else "Edit")
//            }
//
//            if (isEditing) {
//                Button(onClick = {
//                    viewModel.onPlantNameChange(localPlantName)
//                    viewModel.onNutrientsUsedChange(localNutrients)
//                    viewModel.onHarvestTimeChange(localHarvestTime)
//
//                    viewModel.updatePlant()
//                    navController.popBackStack()
//                }) {
//                    Text("Simpan")
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = { viewModel.deletePlant() },
//            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Hapus Tanaman", color = Color.White)
//        }
//
//        if (state.errorMessage != null) {
//            Spacer(modifier = Modifier.height(8.dp))
//            Text("Error: ${state.errorMessage}", color = Color.Red)
//        }
//    }
}

@Composable
fun InfoRow(label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.SemiBold)
        if (isEditing) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(value, modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
