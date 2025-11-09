package com.composeunstyled

import androidx.compose.runtime.Composable

/**
 * A multiplatform version of Android's BackHandler.
 *
 * On non-Android devices, the EscapeHandler calls the callback when Escape is pressed on the keyboard.
 */
@Composable
internal expect fun EscapeHandler(callback: () -> Unit)