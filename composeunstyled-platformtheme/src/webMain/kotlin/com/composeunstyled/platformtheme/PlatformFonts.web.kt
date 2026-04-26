/*
 * Copyright (c) 2026 Composable Horizons
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
