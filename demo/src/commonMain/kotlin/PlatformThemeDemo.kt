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
@file:Suppress("ktlint:standard:max-line-length")

package com.composeunstyled.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.currentWindowContainerSize
import com.composeunstyled.platformtheme.EmojiVariant
import com.composeunstyled.platformtheme.SpokenLanguage
import com.composeunstyled.platformtheme.WebFontOptions
import com.composeunstyled.platformtheme.buildPlatformTheme
import com.composeunstyled.platformtheme.heading1
import com.composeunstyled.platformtheme.heading2
import com.composeunstyled.platformtheme.heading3
import com.composeunstyled.platformtheme.heading4
import com.composeunstyled.platformtheme.heading5
import com.composeunstyled.platformtheme.heading6
import com.composeunstyled.platformtheme.heading7
import com.composeunstyled.platformtheme.heading8
import com.composeunstyled.platformtheme.heading9
import com.composeunstyled.platformtheme.text1
import com.composeunstyled.platformtheme.text2
import com.composeunstyled.platformtheme.text3
import com.composeunstyled.platformtheme.text4
import com.composeunstyled.platformtheme.text5
import com.composeunstyled.platformtheme.text6
import com.composeunstyled.platformtheme.text7
import com.composeunstyled.platformtheme.text8
import com.composeunstyled.platformtheme.text9
import com.composeunstyled.platformtheme.textStyles
import com.composeunstyled.theme.Theme
import com.composeunstyled.Text as ThemedText

private val PlatformTheme = buildPlatformTheme(
  webFontOptions = WebFontOptions(
    supportedLanguages = listOf(
      SpokenLanguage.Korean,
      SpokenLanguage.Japanese,
      SpokenLanguage.ChineseSimplified,
    ),
    emojiVariant = EmojiVariant.Colored,
  ),
)

@Composable
fun PlatformThemeDemo() {
  PlatformTheme {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
      TypographyDemo()
      TextStylesDemo()
    }
  }
}

@Composable
fun TypographyDemo() {
  ThemedText("Typography", style = Theme[textStyles][text9])

  ThemedText(
    "The quick brown fox jumps over the lazy dog 😊🦊😘",
    style = Theme[textStyles][heading9],
  )
  ThemedText("The quick brown fox jumps over the lazy dog 😊🦊😘", style = Theme[textStyles][text9])

  ThemedText("Multilanguage", style = Theme[textStyles][text9])

  ThemedText("Greek: Η γρήγορη καφέ αλεπού πηδά πάνω από το τεμπέλικο σκυλί")
  ThemedText("Korean: 빠른 갈색 여우가 게으른 개를 뛰어넘습니다")
  ThemedText(
    "Japanese: あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン",
  )
  ThemedText("Chinese Simplified: 敏捷的棕色狐狸跳过懒狗")
  ThemedText("Chinese Traditional: 敏捷的棕色狐狸跳過懶狗")
}

@Composable
private fun TextStylesDemo() {
  Column(
    modifier = Modifier.fillMaxWidth().widthIn(max = 1200.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    val isWide = currentWindowContainerSize().width >= 600.dp
    val orientation = if (isWide) StackOrientation.Horizontal else StackOrientation.Vertical
    ThemedText("Text Styles", style = Theme[textStyles][text9])

    Stack(
      orientation = orientation,
      modifier = Modifier.fillMaxWidth(),
      spacing = 24.dp,
    ) {
      val text: String? = null
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ThemedText(text ?: "Text 9", style = Theme[textStyles][text9])
        ThemedText(text ?: "Text 8", style = Theme[textStyles][text8])
        ThemedText(text ?: "Text 7", style = Theme[textStyles][text7])
        ThemedText(text ?: "Text 6", style = Theme[textStyles][text6])
        ThemedText(text ?: "Text 5", style = Theme[textStyles][text5])
        ThemedText(text ?: "Text 4", style = Theme[textStyles][text4])
        ThemedText(text ?: "Text 3", style = Theme[textStyles][text3])
        ThemedText(text ?: "Text 2", style = Theme[textStyles][text2])
        ThemedText(text ?: "Text 1", style = Theme[textStyles][text1])
      }
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ThemedText(text ?: "Heading 9", style = Theme[textStyles][heading9])
        ThemedText(text ?: "Heading 8", style = Theme[textStyles][heading8])
        ThemedText(text ?: "Heading 7", style = Theme[textStyles][heading7])
        ThemedText(text ?: "Heading 6", style = Theme[textStyles][heading6])
        ThemedText(text ?: "Heading 5", style = Theme[textStyles][heading5])
        ThemedText(text ?: "Heading 4", style = Theme[textStyles][heading4])
        ThemedText(text ?: "Heading 3", style = Theme[textStyles][heading3])
        ThemedText(text ?: "Heading 2", style = Theme[textStyles][heading2])
        ThemedText(text ?: "Heading 1", style = Theme[textStyles][heading1])
      }
    }
  }
}
