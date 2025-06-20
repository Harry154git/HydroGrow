package com.pemrogamanmobile.hydrogrow.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pemrogamanmobile.hydrogrow.R
import com.pemrogamanmobile.hydrogrow.data.DataSource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun HomeScreen(
    navigateToProfile: () -> Unit,
    navigateToMakeHydroponic: () -> Unit,
    navigateToHydroponic: (String, String) -> Unit,
    navigateToPlant: (String, String) -> Unit,
    navigateToNews: () -> Unit,
    navigateToMakePlant: () -> Unit
) {
    val context = LocalContext.current
    val dataSource = remember { DataSource.getInstance(context) }
    val user = remember { dataSource.getCurrentUser() }

    // Amati stateList secara langsung agar recomposition terjadi saat data berubah
    val hydroponicList by remember { derivedStateOf { dataSource.hydroponicStateList } }
    val plantList by remember { derivedStateOf { dataSource.loadPlantData() } }

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tidak ada pengguna yang sedang login.")
        }
        return
    }

    val userGarden = hydroponicList.filter { it.idgarden == user.idgarden }
    val userPlants = plantList.filter { it.idgarden == user.idgarden }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navigateToProfile() }
                    .padding(8.dp)
            ) {
                ProfilCard(name = "Selamat datang, ${user.name}", imageResId = R.drawable.user)
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.hydroponic), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.make_hydroponic),
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.clickable { navigateToMakeHydroponic() }
                )
            }
        }

        items(userGarden) { garden ->
            HydroponicCard(
                name = garden.gardenName,
                imageResId = R.drawable.hydroponic_image,
                onClick = {
                    navigateToHydroponic(garden.gardenName, garden.idgarden.toString())
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.your_plant), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Tambah Tanaman",
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.clickable { navigateToMakePlant() }
                )
            }
        }

        items(userPlants) { plant ->
            PlantCard(
                name = plant.plantName,
                imageResId = R.drawable.lettuce_image,
                onClick = {
                    navigateToPlant(plant.plantName, plant.plantName)
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.news), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.more_news),
                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.clickable { navigateToNews() }
                )
            }
        }

        item {
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
}
