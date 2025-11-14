package com.composeunstyled.platformtheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import com.composables.composeunstyled_platformtheme.generated.resources.NotoColorEmoji
import com.composables.composeunstyled_platformtheme.generated.resources.NotoEmoji
import com.composables.composeunstyled_platformtheme.generated.resources.NotoSans
import com.composables.composeunstyled_platformtheme.generated.resources.NotoSansJP
import com.composables.composeunstyled_platformtheme.generated.resources.NotoSansKR
import com.composables.composeunstyled_platformtheme.generated.resources.NotoSansSC
import com.composables.composeunstyled_platformtheme.generated.resources.NotoSansTC
import com.composables.composeunstyled_platformtheme.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.preloadFont


internal actual val PlatformFonts.SansSerif: FontFamily
    @Composable
    get() {
        val fontState = LocalPlatformFontsState.current
        return if (fontState.fontsLoaded) {
            FontFamily(Font(Res.font.NotoSans))
        } else {
            FontFamily.SansSerif
        }
    }

@OptIn(ExperimentalResourceApi::class)
@Composable
internal actual fun loadPlatformFonts(webFontOptions: WebFontOptions): PlatformFontsState {
    val state = remember { PlatformFontsState(fontsReady = false) }

    val fontsToLoad = remember {
        buildList {
            add(Res.font.NotoSans)

            when (webFontOptions.emojiVariant) {
                EmojiVariant.Monochrome -> add(Res.font.NotoEmoji)
                EmojiVariant.Colored -> add(Res.font.NotoColorEmoji)
                EmojiVariant.None -> {}
            }

            if (SpokenLanguage.Korean in webFontOptions.supportedLanguages) {
                add(Res.font.NotoSansKR)
            }
            if (SpokenLanguage.Japanese in webFontOptions.supportedLanguages) {
                add(Res.font.NotoSansJP)
            }
            if (SpokenLanguage.ChineseSimplified in webFontOptions.supportedLanguages) {
                add(Res.font.NotoSansSC)
            }
            if (SpokenLanguage.ChineseTraditional in webFontOptions.supportedLanguages) {
                add(Res.font.NotoSansTC)
            }
        }
    }

    val fontStates = fontsToLoad.map { resource ->
        preloadFont(resource)
    }

    if (fontStates.none { it.value == null }) {
        val fontFamilyResolver = LocalFontFamilyResolver.current

        LaunchedEffect(Unit) {
            fontStates.forEach { state ->
                val fontFamily = FontFamily(state.value!!)
                fontFamilyResolver.preload(fontFamily)
            }
            state.fontsLoaded = true
        }
    }

    return state
}
