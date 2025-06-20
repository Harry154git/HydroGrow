package com.pemrogamanmobile.hydrogrow.hydroponicpage

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pemrogamanmobile.hydrogrow.R

@Composable
fun PlantCard(
    name: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(2.dp, Color.Green)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.lettuce_image),
                contentDescription = name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
            )

            Text(
                text = name,
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                color = Color.Black
            )
        }
    }
}