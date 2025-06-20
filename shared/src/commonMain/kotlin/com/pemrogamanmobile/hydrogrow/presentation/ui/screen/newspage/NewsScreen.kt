package com.pemrogamanmobile.hydrogrow.newspage

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pemrogamanmobile.hydrogrow.data.DataSource

@Composable
fun NewsScreen() {
    val context = LocalContext.current
    val dataSource = DataSource.getInstance(context)
    val newsList = dataSource.loadNewsData()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(newsList.chunked(2)) { _, rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { news ->
                    Box(modifier = Modifier.weight(1f)) {
                        NewsCard(
                            imageResId = news.imageResId,
                            newsTitle = news.title
                        ) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.link))
                            context.startActivity(intent)
                        }
                    }
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
