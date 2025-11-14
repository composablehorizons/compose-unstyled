package com.composeunstyled.platformtheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal actual fun loadPlatformFonts(webFontOptions: WebFontOptions): PlatformFontsState {
    return remember { PlatformFontsState(fontsReady = true) }
}
