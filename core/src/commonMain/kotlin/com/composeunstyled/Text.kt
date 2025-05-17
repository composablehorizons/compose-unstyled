package com.composeunstyled

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

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
    overflow: TextOverflow = TextOverflow.Clip
) {
    val currentColor = listOf(
        color, style.color, LocalContentColor.current
    ).firstOrNull { it != Color.Unspecified } ?: Color.Unspecified

    val currentTextAlign = listOf(
        textAlign,
        style.textAlign,
    ).firstOrNull { it != TextAlign.Unspecified } ?: TextAlign.Unspecified

    val currentLineHeight = listOf(
        lineHeight,
        style.lineHeight,
    ).firstOrNull { it != TextUnit.Unspecified } ?: TextUnit.Unspecified

    val currentLetterSpacing = listOf(
        letterSpacing,
        style.letterSpacing,
    ).firstOrNull { it != TextUnit.Unspecified } ?: TextUnit.Unspecified

    val currentStyle by remember(
        style,
        currentTextAlign,
        fontSize,
        currentColor,
        fontWeight,
        fontFamily,
        currentLineHeight
    ) {
        mutableStateOf(
            style.copy(
                textAlign = currentTextAlign,
                fontSize = fontSize,
                color = currentColor,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                lineHeight = currentLineHeight,
                letterSpacing = currentLetterSpacing
            )
        )
    }
    BasicText(
        text = text,
        modifier = modifier,
        style = currentStyle,
        minLines = minLines,
        maxLines = maxLines,
        overflow = overflow,
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
    overflow: TextOverflow = TextOverflow.Clip
) {
    val currentColor = listOf(
        color, style.color, LocalContentColor.current
    ).firstOrNull { it != Color.Unspecified } ?: Color.Unspecified

    val currentTextAlign = listOf(
        textAlign,
        style.textAlign,
    ).firstOrNull { it != TextAlign.Unspecified } ?: TextAlign.Unspecified

    val currentLetterSpacing = listOf(
        letterSpacing,
        style.letterSpacing,
    ).firstOrNull { it != TextUnit.Unspecified } ?: TextUnit.Unspecified

    val currentLineHeight = listOf(
        lineHeight,
        style.lineHeight,
    ).firstOrNull { it != TextUnit.Unspecified } ?: TextUnit.Unspecified

    val currentStyle by remember(
        style,
        currentTextAlign,
        fontSize,
        currentColor,
        fontWeight,
        fontFamily,
        currentLineHeight,
        currentLetterSpacing
    ) {
        mutableStateOf(
            style.copy(
                textAlign = currentTextAlign,
                fontSize = fontSize,
                color = currentColor,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                lineHeight = currentLineHeight,
                letterSpacing = currentLetterSpacing
            )
        )
    }
    BasicText(
        text = text,
        modifier = modifier,
        style = currentStyle,
        minLines = minLines,
        maxLines = maxLines,
        overflow = overflow,
    )
}
