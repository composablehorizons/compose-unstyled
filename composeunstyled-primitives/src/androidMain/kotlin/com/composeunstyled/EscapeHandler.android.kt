package com.composeunstyled

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
internal actual fun EscapeHandler(callback: () -> Unit) {
    BackHandler { callback() }
}