package com.pemrogamanmobile.hydrogrow.hydroponicpage

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pemrogamanmobile.hydrogrow.R
import com.pemrogamanmobile.hydrogrow.data.DataSource
import com.pemrogamanmobile.hydrogrow.data.Plant

@Composable
fun EditScreen(
    kebunId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val dataSource = DataSource.getInstance(context)
    val originalKebun = remember { dataSource.getKebunById(kebunId) }

    if (originalKebun == null) {
        Text("Kebun tidak ditemukan.")
        return
    }

    var kebun by remember { mutableStateOf(originalKebun) }
    var isEditing by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = kebun.gardenName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { navController.popBackStack() }) {
                Text("Kembali")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(R.drawable.hydroponic_image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoRow("Nama Kebun:", kebun.gardenName, isEditing) { kebun = kebun.copy(gardenName = it) }
        InfoRow("Luas Kebun:", kebun.gardenSize.toString(), isEditing) {
            kebun = kebun.copy(gardenSize = it.toDoubleOrNull() ?: kebun.gardenSize)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Tanaman yang Ditanam:", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.Green, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            kebun.plants.forEach { plant ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(plant.plantName, modifier = Modifier.weight(1f))
                    if (isEditing) {
                        TextButton(onClick = {
                            kebun = kebun.copy(plants = kebun.plants - plant)
                        }) {
                            Text("Hapus", color = Color.Red)
                        }
                    }
                }
            }

            if (isEditing) {
                Button(onClick = {
                    val newPlant = Plant(
                        plantName = "Tanaman Baru",
                        idgarden = kebun.idgarden,
                        nutrientsUsed = "Belum diisi",
                        harvestTime = "Belum diisi"
                    )
                    kebun = kebun.copy(plants = kebun.plants + newPlant)
                }) {
                    Text("Tambah Tanaman")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        InfoRow("Tipe Hidroponik:", kebun.hydroponicType, isEditing) {
            kebun = kebun.copy(hydroponicType = it)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { isEditing = true }) {
                Text("Edit")
            }
            Button(onClick = {
                dataSource.updateKebun(kebun)
                isEditing = false
            }) {
                Text("Simpan")
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = label, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
        if (isEditing) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(4.dp)
            )
        } else {
            Text(text = value, modifier = Modifier.weight(1f))
        }
    }
}
