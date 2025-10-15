package com.composeunstyled.demo

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.composeunstyled.outline

@Composable
fun OutlineBasicDemo() {
    SimpleButton(
        modifier = Modifier.outline(
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        )
    )
}

@Composable
fun OutlineWidthDemo() {
    SimpleButton(
        modifier = Modifier.outline(
            width = 1.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        )
    )
    SimpleButton(
        modifier = Modifier.outline(
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        )
    )
    SimpleButton(
        modifier = Modifier.outline(
            width = 4.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        )
    )
}

@Composable
fun OutlineShapeDemo() {
    SimpleButton(
        shape = RectangleShape,
        modifier = Modifier.outline(
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RectangleShape,
            offset = 2.dp
        )
    )
    SimpleButton(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.outline(
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        )
    )
    SimpleButton(
        shape = CircleShape,
        modifier = Modifier.outline(
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = CircleShape,
            offset = 2.dp
        )
    )
}

@Composable
fun OutlineOffsetDemo() {
    SimpleButton(
        modifier = Modifier.outline(
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 0.dp
        )
    )
    SimpleButton(
        modifier = Modifier.outline(
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 4.dp
        )
    )
    SimpleButton(
        modifier = Modifier.outline(
            width = 2.dp,
            color = Color(0xFF3B82F6),
            shape = RoundedCornerShape(8.dp),
            offset = 8.dp
        )
    )
}

@Composable
fun OutlineColorDemo() {
    SimpleButton(
        modifier = Modifier.outline(
            width = 2.dp,
            color = Color(0xFFEF4444), // red-500
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        )
    )
    SimpleButton(
        modifier = Modifier.outline(
            width = 2.dp,
            color = Color(0xFF10B981), // emerald-500
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        )
    )
    SimpleButton(
        modifier = Modifier.outline(
            width = 2.dp,
            color = Color(0xFF8B5CF6), // violet-500
            shape = RoundedCornerShape(8.dp),
            offset = 2.dp
        )
    )
}
