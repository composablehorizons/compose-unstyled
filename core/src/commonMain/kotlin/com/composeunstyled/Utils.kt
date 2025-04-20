package com.composeunstyled

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type

internal val AppearInstantly: EnterTransition = fadeIn(animationSpec = tween(durationMillis = 0))
internal val DisappearInstantly: ExitTransition = fadeOut(animationSpec = tween(durationMillis = 0))

val LocalContentColor = compositionLocalOf { Color.Black }

internal val KeyEvent.isKeyDown: Boolean
    get() = type == KeyEventType.KeyDown