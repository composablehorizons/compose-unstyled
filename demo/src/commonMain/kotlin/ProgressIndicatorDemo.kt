package com.composeunstyled.demo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.ProgressBar
import com.composeunstyled.ProgressIndicator
import kotlinx.coroutines.delay

@Composable
fun ProgressIndicatorDemo() {
    var hasProgressed by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (hasProgressed) 0.85f else 0.2f,
        animationSpec = tween(durationMillis = 450)
    )
    LaunchedEffect(Unit) {
        delay(500)
        hasProgressed = true
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF134E5E), Color(0xFF71B280))))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        ProgressIndicator(
            progress = progress,
            modifier = Modifier.width(400.dp).height(24.dp).shadow(4.dp, RoundedCornerShape(100)),
            shape = RoundedCornerShape(100),
            backgroundColor = Color(0xff176153),
            contentColor = Color(0xffb6eabb),
        ) {
            ProgressBar(shape = RoundedCornerShape(100))
        }
    }
}
