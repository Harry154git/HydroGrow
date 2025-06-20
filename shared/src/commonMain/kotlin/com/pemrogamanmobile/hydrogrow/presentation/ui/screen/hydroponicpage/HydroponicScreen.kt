package com.pemrogamanmobile.hydrogrow.hydroponicpage

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

@Composable
fun HydroponicScreen(navController: NavController, gardenName: String) {
    val context = LocalContext.current
    val dataSource = remember { DataSource.getInstance(context) }

    val kebun = dataSource.hydroponicStateList.find { it.gardenName == gardenName }

    if (kebun == null) {
        Text("Kebun tidak ditemukan.")
        return
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = kebun.gardenName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = { navController.popBackStack() }) {
                Text("Kembali")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(R.drawable.hydroponic_image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { navController.navigate("edit/${kebun.idgarden}") }) {
                Text("Edit")
            }
            Button(onClick = {
                dataSource.deleteKebun(kebun.idgarden)
                navController.popBackStack()
            }) {
                Text("Hapus")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            Toast.makeText(navController.context, "Fitur masih dalam pengembangan", Toast.LENGTH_SHORT).show()
        }) {
            Text("Bertanya tentang hidroponik ini?")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Riwayat bertanya",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        LazyRow {
            items(0) {
                // Kosong karena masih tahap pengembangan
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Jadwal pembersihan dan perawatan",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        CalenderBox()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tanaman yang ada di kebun ini",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        LazyRow {
            items(kebun.plants) { tanaman ->
                PlantCard(
                    name = tanaman.plantName,
                    onClick = { navController.navigate("plant/${tanaman.idgarden}") }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Berita terbaru terkait hidroponik",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Lebih banyak",
                color = Color.Blue,
                modifier = Modifier.clickable { navController.navigate("news") }
            )
        }

        LazyRow {
            items(dataSource.loadNewsData()) { news ->
                com.pemrogamanmobile.hydrogrow.plantpage.NewsCard(newsItem = news, onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse(news.link)
                    }
                    context.startActivity(intent)
                })
            }
        }
    }
}

