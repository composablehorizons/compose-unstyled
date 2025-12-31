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
    overflow: TextOverflow = TextOverflow.Clip,
    autoSize: TextAutoSize? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {
    val currentStyle = style.mergeThemed(
        textAlign = textAlign,
        fontSize = fontSize,
        color = color,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing
    )

    BasicText(
        text = text,
        modifier = modifier,
        style = currentStyle,
        minLines = minLines,
        maxLines = maxLines,
        overflow = overflow,
        autoSize = autoSize,
        onTextLayout = onTextLayout
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
    overflow: TextOverflow = TextOverflow.Clip,
    autoSize: TextAutoSize? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {

    val currentStyle = style.mergeThemed(
        textAlign = textAlign,
        fontSize = fontSize,
        color = color,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing
    )

    BasicText(
        text = text,
        modifier = modifier,
        style = currentStyle,
        minLines = minLines,
        maxLines = maxLines,
        overflow = overflow,
        autoSize = autoSize,
        onTextLayout = onTextLayout
    )
}

@Composable
internal fun TextStyle.mergeThemed(
    textAlign: TextAlign,
    fontSize: TextUnit,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    lineHeight: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
): TextStyle {
    val textStyleColor = this.color
    val contentColor = LocalContentColor.current

    val finalColor = if (ComposeUnstyledFlags.strictTextColorResolutionOrder) {
        listOf(color, textStyleColor).firstOrNull { it.isSpecified } ?: contentColor
    } else {
        listOf(color, contentColor, textStyleColor).firstOrNull { it.isSpecified } ?: Color.Unspecified
    }

    return this.merge(
        textAlign = listOf(textAlign, this.textAlign)
            .firstOrNull { it != TextAlign.Unspecified } ?: TextAlign.Unspecified,
        fontSize = listOf(fontSize, this.fontSize).firstOrNull { it.isSpecified } ?: TextUnit.Unspecified,
        color = finalColor,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        lineHeight = listOf(lineHeight, this.lineHeight).firstOrNull { it.isSpecified } ?: TextUnit.Unspecified,
        letterSpacing = listOf(letterSpacing, this.letterSpacing)
            .firstOrNull { it.isSpecified } ?: TextUnit.Unspecified)
}
