package com.composeunstyled

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

val LocalContentColor = compositionLocalOf { Color.Unspecified }
val LocalTextStyle = compositionLocalOf { TextStyle.Default }

@Composable
fun ProvideContentColor(color: Color, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalContentColor provides color, content = content)
}

@Composable
fun ProvideTextStyle(textStyle: TextStyle, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalTextStyle provides textStyle, content = content)
}
