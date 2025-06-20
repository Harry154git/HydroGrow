package com.pemrogamanmobile.hydrogrow.plantpage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pemrogamanmobile.hydrogrow.R
import com.pemrogamanmobile.hydrogrow.data.DataSource
import com.pemrogamanmobile.hydrogrow.hydroponicpage.InfoRow

@Composable
fun EditScreen(navController: NavController, plantName: String) {
    val context = LocalContext.current
    val dataSource = remember { DataSource.getInstance(context) }

    val plant = dataSource.hydroponicStateList
        .flatMap { it.plants }
        .find { it.plantName == plantName }

    if (plant == null) {
        Text("Tanaman tidak ditemukan.")
        return
    }

    var jenis by remember(plant) { mutableStateOf(plant.plantName) }
    var nutrisi by remember(plant) { mutableStateOf(plant.nutrientsUsed) }
    var panen by remember(plant) { mutableStateOf(plant.harvestTime) }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Edit Tanaman", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Button(onClick = { navController.popBackStack() }) {
                Text("Kembali")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Tanaman",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        InfoRow("Nama Tanaman:", jenis, isEditing) { jenis = it }
        InfoRow("Nutrisi:", nutrisi, isEditing) { nutrisi = it }
        InfoRow("Masa Panen:", panen, isEditing) { panen = it }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { isEditing = !isEditing }) {
                Text(if (isEditing) "Batal" else "Edit")
            }

            if (isEditing) {
                Button(onClick = {
                    // Update data tanaman di DataSource
                    val updatedPlant = plant.copy(
                        plantName = jenis,
                        nutrientsUsed = nutrisi,
                        harvestTime = panen
                    )
                    dataSource.deletePlant(plant.plantName)
                    dataSource.addPlantToGarden(plant.idgarden, updatedPlant)

                    isEditing = false
                    navController.popBackStack()
                }) {
                    Text("Simpan")
                }
            }
        }
    }
}