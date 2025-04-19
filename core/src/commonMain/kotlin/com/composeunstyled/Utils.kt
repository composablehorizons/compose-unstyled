package com.composeunstyled

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

internal val AppearInstantly: EnterTransition = fadeIn(animationSpec = tween(durationMillis = 0))
internal val DisappearInstantly: ExitTransition = fadeOut(animationSpec = tween(durationMillis = 0))