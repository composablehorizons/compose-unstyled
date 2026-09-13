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
import com.composeunstyled.Text
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

private val PlatformTheme = buildPlatformTheme(
  webFontOptions = WebFontOptions(
    supportedLanguages = listOf(
      SpokenLanguage.Korean,
      SpokenLanguage.Japanese,
      SpokenLanguage.ChineseSimplified,
    ),
    emojiVariant = EmojiVariant.Monochrome,
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
  Text("TYPOGRAPHY", style = Theme[textStyles][text9])

  Text(
    "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG 😊🦊😘",
    style = Theme[textStyles][heading9],
  )
  Text("THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG 😊🦊😘", style = Theme[textStyles][text9])

  Text("MULTILANGUAGE", style = Theme[textStyles][text9])

  Text("GREEK: Η ΓΡΉΓΟΡΗ ΚΑΦΈ ΑΛΕΠΟΎ ΠΗΔΆ ΠΆΝΩ ΑΠΌ ΤΟ ΤΕΜΠΈΛΙΚΟ ΣΚΥΛΊ")
  Text("KOREAN: 빠른 갈색 여우가 게으른 개를 뛰어넘습니다")
  Text(
    "JAPANESE: あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲン",
  )
  Text("CHINESE SIMPLIFIED: 敏捷的棕色狐狸跳过懒狗")
  Text("CHINESE TRADITIONAL: 敏捷的棕色狐狸跳過懶狗")
}

@Composable
private fun TextStylesDemo() {
  Column(
    modifier = Modifier.fillMaxWidth().widthIn(max = 1200.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    val isWide = currentWindowContainerSize().width >= 600.dp
    val orientation = if (isWide) StackOrientation.Horizontal else StackOrientation.Vertical
    Text("TEXT STYLES", style = Theme[textStyles][text9])

    Stack(
      orientation = orientation,
      modifier = Modifier.fillMaxWidth(),
      spacing = 24.dp,
    ) {
      val text: String? = null
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text ?: "TEXT 9", style = Theme[textStyles][text9])
        Text(text ?: "TEXT 8", style = Theme[textStyles][text8])
        Text(text ?: "TEXT 7", style = Theme[textStyles][text7])
        Text(text ?: "TEXT 6", style = Theme[textStyles][text6])
        Text(text ?: "TEXT 5", style = Theme[textStyles][text5])
        Text(text ?: "TEXT 4", style = Theme[textStyles][text4])
        Text(text ?: "TEXT 3", style = Theme[textStyles][text3])
        Text(text ?: "TEXT 2", style = Theme[textStyles][text2])
        Text(text ?: "TEXT 1", style = Theme[textStyles][text1])
      }
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text ?: "HEADING 9", style = Theme[textStyles][heading9])
        Text(text ?: "HEADING 8", style = Theme[textStyles][heading8])
        Text(text ?: "HEADING 7", style = Theme[textStyles][heading7])
        Text(text ?: "HEADING 6", style = Theme[textStyles][heading6])
        Text(text ?: "HEADING 5", style = Theme[textStyles][heading5])
        Text(text ?: "HEADING 4", style = Theme[textStyles][heading4])
        Text(text ?: "HEADING 3", style = Theme[textStyles][heading3])
        Text(text ?: "HEADING 2", style = Theme[textStyles][heading2])
        Text(text ?: "HEADING 1", style = Theme[textStyles][heading1])
      }
    }
  }
}
