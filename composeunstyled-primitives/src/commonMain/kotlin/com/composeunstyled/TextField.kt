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
package com.composeunstyled

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

class TextFieldScope {
  internal var innerTextField: (@Composable () -> Unit)? = null
  internal var text: String by mutableStateOf("")
  internal var editable: Boolean by mutableStateOf(true)
  internal var visualTransformation: VisualTransformation by
    mutableStateOf(VisualTransformation.None)
  internal var textAlignment by mutableStateOf(TextAlign.Unspecified)

  internal var minLines: Int by mutableStateOf(1)
  internal var maxLines: Int by mutableStateOf(Int.MAX_VALUE)

  internal var isLeadingFocused by mutableStateOf(false)
  internal var isTrailingFocused by mutableStateOf(false)
}

@Composable
fun TextFieldScope.TextInput(
  modifier: Modifier = Modifier,
  shape: Shape = RectangleShape,
  backgroundColor: Color = Color.Unspecified,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  label: String? = null,
  placeholder: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null,
  trailing: (@Composable () -> Unit)? = null,
  verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
  Row(
    modifier = modifier
      .clip(shape)
      .background(backgroundColor)
      .pointerHoverIcon(PointerIcon.Text) then buildModifier {
      if (label != null) {
        add(Modifier.semantics { contentDescription = label })
      }
    }.padding(contentPadding),
    verticalAlignment = verticalAlignment,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    if (leading != null) {
      Box(
        Modifier.pointerHoverIcon(PointerIcon.Default).onFocusChanged {
          isLeadingFocused = it.hasFocus
        },
      ) {
        leading()
      }
    }
    val contentAlignment: Alignment = when (textAlignment) {
      TextAlign.End -> Alignment.TopEnd
      TextAlign.Center -> Alignment.Center
      else -> Alignment.TopStart
    }
    Box(contentAlignment = contentAlignment, modifier = Modifier.weight(1f)) {
      if (editable) {
        innerTextField!!.invoke()
      } else {
        val transformedText = visualTransformation.filter(AnnotatedString(text)).text.text
        SelectionContainer {
          BasicText(transformedText)
        }
      }

      if (placeholder != null && text.isEmpty()) {
        Box(Modifier.matchParentSize(), contentAlignment = contentAlignment) {
          placeholder()
        }
      }
    }
    if (trailing != null) {
      Box(
        Modifier.pointerHoverIcon(PointerIcon.Default)
          .onFocusChanged { isTrailingFocused = it.hasFocus },
      ) {
        trailing()
      }
    }
  }
}

@Composable
fun UnstyledTextField(
  state: TextFieldState,
  modifier: Modifier = Modifier,
  editable: Boolean = true,
  cursorBrush: Brush = SolidColor(Color.Black),
  textStyle: TextStyle = TextStyle.Default,
  textAlign: TextAlign = TextAlign.Unspecified,
  lineHeight: TextUnit = TextUnit.Unspecified,
  fontSize: TextUnit = TextUnit.Unspecified,
  letterSpacing: TextUnit = TextUnit.Unspecified,
  fontWeight: FontWeight? = null,
  fontFamily: FontFamily? = null,
  textDecoration: TextDecoration? = textStyle.textDecoration,
  singleLine: Boolean = false,
  minLines: Int = 1,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  onKeyboardAction: KeyboardActionHandler? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  interactionSource: MutableInteractionSource? = null,
  textColor: Color = Color.Unspecified,
  scrollState: ScrollState = rememberScrollState(),
  content: @Composable TextFieldScope.() -> Unit,
) {
  val scope = remember { TextFieldScope() }

  scope.text = state.text.toString()
  scope.editable = editable
  scope.visualTransformation = visualTransformation

  val newTextStyle = textStyle.mergeSafely(
    textAlign = textAlign,
    fontSize = fontSize,
    fontWeight = fontWeight,
    fontFamily = fontFamily,
    textDecoration = textDecoration,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
    color = textColor,
  )
  scope.textAlignment = newTextStyle.textAlign
  scope.minLines = minLines
  scope.maxLines = maxLines

  if (editable) {
    val inputIsFocused = scope.isTrailingFocused.not() && scope.isLeadingFocused.not()

    BasicTextField(
      scrollState = scrollState,
      state = state,
      interactionSource = interactionSource,
      textStyle = newTextStyle,
      outputTransformation = {
        val transformedText =
          visualTransformation.filter(AnnotatedString(originalText.toString()))
        val newText = transformedText.text.text
        val mapping = transformedText.offsetMapping
        val originalLength = originalText.length

        // Apply edits from end to start so index shifts don't affect upcoming edits.
        for (index in originalLength - 1 downTo 0) {
          val transformedStart = mapping.originalToTransformed(index)
          val transformedEnd = mapping.originalToTransformed(index + 1)
          val replacement = newText.substring(transformedStart, transformedEnd)
          replace(index, index + 1, replacement)
        }

        val suffixStart = mapping.originalToTransformed(originalLength)
        if (suffixStart < newText.length) {
          replace(length, length, newText.substring(suffixStart))
        }
      },
      inputTransformation = InputTransformation {
        // block any value changes, unless the actual text input is focused
        if (inputIsFocused.not()) {
          revertAllChanges()
        }
      },
      modifier = modifier.semantics(mergeDescendants = true) {},
      cursorBrush = cursorBrush,
      lineLimits = if (singleLine) {
        TextFieldLineLimits.SingleLine
      } else {
        TextFieldLineLimits.MultiLine(minLines, maxLines)
      },
      keyboardOptions = keyboardOptions,
      onKeyboardAction = onKeyboardAction,
      decorator = { innerTextField ->
        scope.innerTextField = innerTextField
        Column(
          Modifier
            // we are handling pointerIcons in TextInput()
            .pointerHoverIcon(PointerIcon.Default),
        ) {
          scope.content()
        }
      },
    )
  } else {
    Column(modifier) {
      scope.content()
    }
  }
}

@Composable
internal fun TextStyle.mergeSafely(
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

  val finalColor = listOf(color, textStyleColor).firstOrNull { it.isSpecified }
    ?: Color.Unspecified

  return this.merge(
    textAlign = listOf(textAlign, this.textAlign)
      .firstOrNull { it != TextAlign.Unspecified } ?: TextAlign.Unspecified,
    fontSize = listOf(fontSize, this.fontSize)
      .firstOrNull { it.isSpecified } ?: TextUnit.Unspecified,
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
