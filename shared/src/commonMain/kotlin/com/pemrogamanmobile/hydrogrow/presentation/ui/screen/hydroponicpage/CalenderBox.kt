package com.pemrogamanmobile.hydrogrow.hydroponicpage

import android.widget.CalendarView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CalenderBox() {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color.Green,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp)
    ) {
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
