package com.composables.core

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal actual fun NoOverscroll(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalOverscrollConfiguration provides null,
        content = content
    )
}