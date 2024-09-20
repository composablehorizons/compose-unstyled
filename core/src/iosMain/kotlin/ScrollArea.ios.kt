package com.composables.core

import androidx.compose.runtime.Composable

@Composable
internal actual fun NoOverscroll(content: @Composable () -> Unit) {
    content()
}