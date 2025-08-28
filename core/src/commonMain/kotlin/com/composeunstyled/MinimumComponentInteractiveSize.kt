package com.composeunstyled

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

internal val LocalMinimumComponentInteractiveSize = compositionLocalOf { Dp.Unspecified }

/**
 * Sets the minimum size of the composable. The value is resolved from the given Theme.
 *
 * To specify this value, set the value you want via the [com.composeunstyled.theme.buildTheme]'s [defaultMinimumInteractiveComponentSize] property.
 */
@Composable
fun Modifier.minimumInteractiveComponentSize(): Modifier {
    val size = LocalMinimumComponentInteractiveSize.current
    return this then Modifier.sizeIn(minWidth = size, minHeight = size)
}