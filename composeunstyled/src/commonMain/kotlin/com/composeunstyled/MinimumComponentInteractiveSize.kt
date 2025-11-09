package com.composeunstyled

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.composeunstyled.theme.ComponentInteractiveSize

internal val LocalMinimumComponentInteractiveSize = compositionLocalOf { ComponentInteractiveSize(Dp.Unspecified) }

/**
 * Sets the minimum size of the composable according to the currently resolved [com.composeunstyled.theme.Theme]
 *
 * To specify this value, set the value you want via the [com.composeunstyled.theme.buildTheme]'s [defaultComponentInteractiveSize] property.
 */
@Composable
fun Modifier.minimumInteractiveComponentSize(): Modifier {
    val size = LocalMinimumComponentInteractiveSize.current

    return this then if (isTouchDevice) {
        Modifier.sizeIn(minWidth = size.touchInteractionSize, minHeight = size.touchInteractionSize)
    } else {
        Modifier.sizeIn(minWidth = size.nonTouchInteractionSize, minHeight = size.nonTouchInteractionSize)
    }
}
