package com.composeunstyled.platformtheme

import androidx.compose.foundation.Indication
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal actual fun rememberPlatformIndication(tint: Color): Indication {
    error("Platform indication is not supported on this platform")
}
