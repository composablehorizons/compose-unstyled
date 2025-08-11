package com.composeunstyled.demo

import androidx.compose.material3.ripple
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.rememberColoredIndication
import composeunstyled.demo.generated.resources.Inter
import composeunstyled.demo.generated.resources.Res
import org.jetbrains.compose.resources.Font

val DemoTheme = buildTheme {
    defaultIndication = rememberColoredIndication(
        hoveredColor = Color.White.copy(alpha = 0.3f),
        pressedColor = Color.White.copy(alpha = 0.5f),
        focusedColor = Color.Black.copy(alpha = 0.1f),
    )

    defaultTextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Inter)),
    )

    defaultIndication = ripple()
}
