package com.composeunstyled

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent

/**
 * Global keyboard listener. Does not block any key presses.
 *
 * As opposed to Compose's onKeyEvent, this will be called no matter if we have focus or not.
 *
 */
@Composable
internal expect fun KeyEventObserver(onEvent: (KeyEvent) -> Boolean)