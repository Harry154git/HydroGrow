package com.pemrogamanmobile.hydrogrow.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pemrogamanmobile.hydrogrow.R

@Composable
fun PlantCard(
    imageResId: Int,
    name: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.welcome),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .border(
                            width = 2.dp,
                            color = Color(0xFF4CAF50),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp)
                )
            }
        }
    }
}
