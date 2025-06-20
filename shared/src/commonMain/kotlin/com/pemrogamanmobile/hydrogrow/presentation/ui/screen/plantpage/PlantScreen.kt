package com.pemrogamanmobile.hydrogrow.plantpage

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pemrogamanmobile.hydrogrow.R
import com.pemrogamanmobile.hydrogrow.data.DataSource

@Composable
fun PlantScreen(navController: NavController, plantName: String) {
    val context = LocalContext.current
    val dataSource = remember { DataSource.getInstance(context) }

    val plant = dataSource.loadPlants().find { it.plantName.equals(plantName, ignoreCase = true) }

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
            Text(text = plant?.plantName ?: "Tanaman", fontSize = 24.sp)
            Button(onClick = { navController.navigate("homes") }) {
                Text("Kembali")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.lettuce_image),
            contentDescription = "Tanaman",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                plant?.let {
                    navController.navigate("edit_plant/${it.plantName}")
                } ?: Toast.makeText(context, "Tanaman tidak ditemukan", Toast.LENGTH_SHORT).show()
            }) {
                Text("Edit")
            }

            Button(onClick = {
                plant?.let {
                    dataSource.deletePlant(it.plantName)
                    Toast.makeText(context, "Tanaman dihapus", Toast.LENGTH_SHORT).show()
                    navController.navigate("homes")
                } ?: Toast.makeText(context, "Tanaman tidak ditemukan", Toast.LENGTH_SHORT).show()
            }) {
                Text("Hapus")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            Toast.makeText(context, "Fitur dalam pengembangan", Toast.LENGTH_SHORT).show()
        }) {
            Text("Bertanya tentang tanaman ini?")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Riwayat Bertanya", fontSize = 18.sp)

        LazyRow {
            // Kosong untuk sekarang
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Jadwal Panen Tanaman", fontSize = 18.sp)

        CalenderCard()

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            Toast.makeText(context, "Fitur dalam pengembangan", Toast.LENGTH_SHORT).show()
        }) {
            Text("Ingin mengecek kondisi tanaman?")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow {
            items(3) {
                CheckPlantCard()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Berita Terbaru Terkait Tanaman", fontSize = 18.sp)

        LazyRow {
            items(dataSource.loadNewsData()) { news ->
                NewsCard(newsItem = news, onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse(news.link)
                    }
                    context.startActivity(intent)
                })
            }
        }
    }
}
