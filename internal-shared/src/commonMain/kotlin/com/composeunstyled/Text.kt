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

package com.composeunstyled

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified

/**
 * A themable composable that displays text.
 *
 * For interactive preview & code examples, visit [Text Documentation](https://composeunstyled.com/text).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Text("Hello, World!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
 *
 * ```
 *
 * @param text The text to be displayed.
 * @param modifier Modifier to be applied to the text.
 * @param style The style to be applied to the text.
 * @param textAlign The alignment of the text.
 * @param lineHeight The height of each line of text.
 * @param fontSize The size of the text.
 * @param letterSpacing The spacing between letters.
 * @param fontWeight The weight of the text.
 * @param color The color of the text.
 * @param fontFamily The font family to be used.
 * @param singleLine Whether the text should be displayed in a single line.
 * @param minLines The minimum number of lines to display.
 * @param maxLines The maximum number of lines to display.
 * @param overflow How to handle text overflow.
 */
@Deprecated(
  "This will go to 2.0. Use the version with the Unstyled- prefix instead",
  ReplaceWith(
    "UnstyledText(text, modifier, style, textAlign, lineHeight, fontSize, letterSpacing, " +
      "fontWeight, color, fontFamily, singleLine, minLines, maxLines, onTextLayout, overflow, " +
      "autoSize)",
  ),
)
@Composable
fun Text(
  text: String,
  modifier: Modifier = Modifier,
  style: TextStyle = LocalTextStyle.current,
  textAlign: TextAlign = TextAlign.Unspecified,
  lineHeight: TextUnit = TextUnit.Unspecified,
  fontSize: TextUnit = style.fontSize,
  letterSpacing: TextUnit = style.letterSpacing,
  fontWeight: FontWeight? = style.fontWeight,
  color: Color = Color.Unspecified,
  fontFamily: FontFamily? = style.fontFamily,
  singleLine: Boolean = false,
  minLines: Int = 1,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  onTextLayout: ((TextLayoutResult) -> Unit)? = null,
  overflow: TextOverflow = TextOverflow.Clip,
  autoSize: TextAutoSize? = null,
) {
  UnstyledText(
    text = text,
    modifier = modifier,
    style = style,
    textAlign = textAlign,
    lineHeight = lineHeight,
    fontSize = fontSize,
    letterSpacing = letterSpacing,
    fontWeight = fontWeight,
    color = color,
    fontFamily = fontFamily,
    singleLine = singleLine,
    minLines = minLines,
    maxLines = maxLines,
    onTextLayout = onTextLayout,
    overflow = overflow,
    autoSize = autoSize,
  )
}

@Composable
fun UnstyledText(
  text: String,
  modifier: Modifier = Modifier,
  style: TextStyle = LocalTextStyle.current,
  textAlign: TextAlign = TextAlign.Unspecified,
  lineHeight: TextUnit = TextUnit.Unspecified,
  fontSize: TextUnit = style.fontSize,
  letterSpacing: TextUnit = style.letterSpacing,
  fontWeight: FontWeight? = style.fontWeight,
  color: Color = Color.Unspecified,
  fontFamily: FontFamily? = style.fontFamily,
  textDecoration: TextDecoration? = style.textDecoration,
  singleLine: Boolean = false,
  minLines: Int = 1,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  onTextLayout: ((TextLayoutResult) -> Unit)? = null,
  overflow: TextOverflow = TextOverflow.Clip,
  autoSize: TextAutoSize? = null,
) {
  val currentStyle = style.mergeThemed(
    textAlign = textAlign,
    fontSize = fontSize,
    color = color,
    fontWeight = fontWeight,
    fontFamily = fontFamily,
    textDecoration = textDecoration,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
  )

  BasicText(
    text = text,
    modifier = modifier,
    style = currentStyle,
    minLines = minLines,
    maxLines = maxLines,
    overflow = overflow,
    autoSize = autoSize,
    onTextLayout = onTextLayout,
  )
}

/**
 * A themable composable that displays text.
 *
 * For interactive preview & code examples, visit [Text Documentation](https://composeunstyled.com/text).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Text("Hello, World!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
 *
 * ```
 *
 * @param text The annotated text to be displayed.
 * @param modifier Modifier to be applied to the text.
 * @param style The style to be applied to the text.
 * @param textAlign The alignment of the text.
 * @param fontSize The size of the text.
 * @param letterSpacing The spacing between letters.
 * @param fontWeight The weight of the text.
 * @param color The color of the text.
 * @param lineHeight The height of each line of text.
 * @param fontFamily The font family to be used.
 * @param singleLine Whether the text should be displayed in a single line.
 * @param minLines The minimum number of lines to display.
 * @param maxLines The maximum number of lines to display.
 * @param overflow How to handle text overflow.
 */
@Deprecated(
  "This will go to 2.0. Use the version with the Unstyled- prefix instead",
  ReplaceWith(
    "UnstyledText(text, modifier, style, textAlign, fontSize, letterSpacing, fontWeight, " +
      "color, lineHeight, fontFamily, singleLine, minLines, maxLines, onTextLayout, overflow, " +
      "autoSize)",
  ),
)
@Composable
fun Text(
  text: AnnotatedString,
  modifier: Modifier = Modifier,
  style: TextStyle = LocalTextStyle.current,
  textAlign: TextAlign = TextAlign.Unspecified,
  fontSize: TextUnit = style.fontSize,
  letterSpacing: TextUnit = style.letterSpacing,
  fontWeight: FontWeight? = style.fontWeight,
  color: Color = Color.Unspecified,
  lineHeight: TextUnit = TextUnit.Unspecified,
  fontFamily: FontFamily? = style.fontFamily,
  singleLine: Boolean = false,
  minLines: Int = 1,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  onTextLayout: ((TextLayoutResult) -> Unit)? = null,
  overflow: TextOverflow = TextOverflow.Clip,
  autoSize: TextAutoSize? = null,
) {
  UnstyledText(
    text = text,
    modifier = modifier,
    style = style,
    textAlign = textAlign,
    fontSize = fontSize,
    letterSpacing = letterSpacing,
    fontWeight = fontWeight,
    color = color,
    lineHeight = lineHeight,
    fontFamily = fontFamily,
    singleLine = singleLine,
    minLines = minLines,
    maxLines = maxLines,
    onTextLayout = onTextLayout,
    overflow = overflow,
    autoSize = autoSize,
  )
}

@Composable
fun UnstyledText(
  text: AnnotatedString,
  modifier: Modifier = Modifier,
  style: TextStyle = LocalTextStyle.current,
  textAlign: TextAlign = TextAlign.Unspecified,
  fontSize: TextUnit = style.fontSize,
  letterSpacing: TextUnit = style.letterSpacing,
  fontWeight: FontWeight? = style.fontWeight,
  color: Color = Color.Unspecified,
  lineHeight: TextUnit = TextUnit.Unspecified,
  fontFamily: FontFamily? = style.fontFamily,
  textDecoration: TextDecoration? = style.textDecoration,
  singleLine: Boolean = false,
  minLines: Int = 1,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  onTextLayout: ((TextLayoutResult) -> Unit)? = null,
  overflow: TextOverflow = TextOverflow.Clip,
  autoSize: TextAutoSize? = null,
) {
  val currentStyle = style.mergeThemed(
    textAlign = textAlign,
    fontSize = fontSize,
    color = color,
    fontWeight = fontWeight,
    fontFamily = fontFamily,
    textDecoration = textDecoration,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
  )

  BasicText(
    text = text,
    modifier = modifier,
    style = currentStyle,
    minLines = minLines,
    maxLines = maxLines,
    overflow = overflow,
    autoSize = autoSize,
    onTextLayout = onTextLayout,
  )
}

@Composable
internal fun TextStyle.mergeThemed(
  textAlign: TextAlign,
  fontSize: TextUnit,
  color: Color = Color.Unspecified,
  fontWeight: FontWeight?,
  fontFamily: FontFamily?,
  textDecoration: TextDecoration? = null,
  lineHeight: TextUnit = TextUnit.Unspecified,
  letterSpacing: TextUnit = TextUnit.Unspecified,
): TextStyle {
  val textStyleColor = this.color
  val contentColor = LocalContentColor.current

  val finalColor = listOf(color, textStyleColor).firstOrNull { it.isSpecified } ?: contentColor

  return this.merge(
    textAlign = listOf(textAlign, this.textAlign)
      .firstOrNull { it != TextAlign.Unspecified } ?: TextAlign.Unspecified,
    fontSize = listOf(fontSize, this.fontSize).firstOrNull { it.isSpecified }
      ?: TextUnit.Unspecified,
    color = finalColor,
    fontWeight = fontWeight,
    fontFamily = fontFamily,
    textDecoration = textDecoration ?: this.textDecoration,
    lineHeight = listOf(lineHeight, this.lineHeight).firstOrNull { it.isSpecified }
      ?: TextUnit.Unspecified,
    letterSpacing = listOf(letterSpacing, this.letterSpacing)
      .firstOrNull { it.isSpecified } ?: TextUnit.Unspecified,
  )
}
