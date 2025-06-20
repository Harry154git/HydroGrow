package com.pemrogamanmobile.hydrogrow.plantpage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pemrogamanmobile.hydrogrow.data.NewsItem

@Composable
fun NewsCard(
    newsItem: NewsItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(250.dp)
            .clickable { onClick() }
            .border(2.dp, Color.Green)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = newsItem.imageResId),
                contentDescription = newsItem.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp)
            )

            Text(
                text = newsItem.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = Color.Black
            )
        }
    }
}
