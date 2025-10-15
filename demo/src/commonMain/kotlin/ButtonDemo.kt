package com.composeunstyled.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.composeunstyled.Button
import com.composeunstyled.Icon
import com.composeunstyled.Text

@Composable
fun ButtonDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFFFF6F61), Color(0xFFFF8A65)))),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { },
            backgroundColor = Color(0xFFFFFFFF),
            contentColor = Color(0xFF020817),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.shadow(elevation = 4.dp, RoundedCornerShape(12.dp))
        ) {
            Icon(Lucide.Pencil, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text("Compose")
        }
    }
}
