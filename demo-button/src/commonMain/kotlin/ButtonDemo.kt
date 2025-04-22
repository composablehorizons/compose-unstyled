package com.composeunstyled.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.composables.core.Icon
import com.composeunstyled.Button
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
            Icon(Pencil, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text("Compose")
        }
    }
}

private val Pencil: ImageVector
    get() {
        if (_Pencil != null) {
            return _Pencil!!
        }
        _Pencil = ImageVector.Builder(
            name = "Pencil",
            defaultWidth = 18.dp,
            defaultHeight = 18.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(21.174f, 6.812f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -3.986f, -3.987f)
                lineTo(3.842f, 16.174f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.5f, 0.83f)
                lineToRelative(-1.321f, 4.352f)
                arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.623f, 0.622f)
                lineToRelative(4.353f, -1.32f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.83f, -0.497f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF000000)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15f, 5f)
                lineToRelative(4f, 4f)
            }
        }.build()
        return _Pencil!!
    }

private var _Pencil: ImageVector? = null
