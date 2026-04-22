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
