package com.composeunstyled.platformtheme

import androidx.compose.foundation.Indication
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.composables.compose.ripple.rememberRippleIndication

@Composable
internal actual fun rememberPlatformIndication(tint: Color): Indication {
    return rememberRippleIndication(
        color = tint,
    )
}
