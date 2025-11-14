package com.composeunstyled.platformtheme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.FontFamily

internal object PlatformFonts

internal expect val PlatformFonts.SansSerif: FontFamily

internal class PlatformFontsState(fontsReady: Boolean) {
    var fontsLoaded by mutableStateOf(fontsReady)
        internal set
}

internal val LocalPlatformFontsState = staticCompositionLocalOf { PlatformFontsState(false) }

sealed class SpokenLanguage {
    object Korean : SpokenLanguage()
    object Japanese : SpokenLanguage()
    object ChineseTraditional : SpokenLanguage()
    object ChineseSimplified : SpokenLanguage()
}


sealed class EmojiVariant {
    object None : EmojiVariant()
    object Monochrome : EmojiVariant()
    object Colored : EmojiVariant()
}

@Stable
class WebFontOptions(
    val supportedLanguages: List<SpokenLanguage> = emptyList(),
    val emojiVariant: EmojiVariant = EmojiVariant.Monochrome,
)
