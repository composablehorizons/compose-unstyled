package com.composeunstyled

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

internal val LocalMinimumComponentInteractiveSize = compositionLocalOf { Dp.Unspecified }

@Composable
fun Modifier.minimumInteractiveComponentSize(): Modifier {
    val size = LocalMinimumComponentInteractiveSize.current
    return this then Modifier.sizeIn(minWidth = size, minHeight = size)
}