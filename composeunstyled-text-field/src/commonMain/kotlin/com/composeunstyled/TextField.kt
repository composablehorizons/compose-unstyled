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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

class TextFieldScope {
  internal var innerTextField: (@Composable () -> Unit)? = null
  internal var text: String by mutableStateOf("")
  internal var textAlignment by mutableStateOf(TextAlign.Unspecified)

  internal var isLeadingFocused by mutableStateOf(false)
  internal var isTrailingFocused by mutableStateOf(false)
}

@Composable
fun TextFieldScope.TextInput(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  accessibilityLabel: String? = null,
  placeholder: (@Composable () -> Unit)? = null,
  leading: (@Composable () -> Unit)? = null,
  trailing: (@Composable () -> Unit)? = null,
  verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
  Row(
    modifier = modifier
      .pointerHoverIcon(PointerIcon.Text) then buildModifier {
      if (accessibilityLabel != null) {
        add(Modifier.semantics { contentDescription = accessibilityLabel })
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
      innerTextField!!.invoke()

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
  cursorBrush: Brush = SolidColor(Color.Unspecified),
  textStyle: TextStyle = TextStyle.Default,
  textAlign: TextAlign = TextAlign.Unspecified,
  lineHeight: TextUnit = TextUnit.Unspecified,
  fontSize: TextUnit = TextUnit.Unspecified,
  letterSpacing: TextUnit = TextUnit.Unspecified,
  fontWeight: FontWeight? = null,
  fontFamily: FontFamily? = null,
  textDecoration: TextDecoration? = textStyle.textDecoration,
  lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
  inputTransformation: InputTransformation? = null,
  outputTransformation: OutputTransformation? = null,
  onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
  onKeyboardAction: KeyboardActionHandler? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  interactionSource: MutableInteractionSource? = null,
  textColor: Color = Color.Unspecified,
  scrollState: ScrollState = rememberScrollState(),
  content: @Composable TextFieldScope.() -> Unit,
) {
  val scope = remember { TextFieldScope() }

  scope.text = state.text.toString()

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

  val inputIsFocused = scope.isTrailingFocused.not() && scope.isLeadingFocused.not()
  val focusTransformation = InputTransformation {
    // block any value changes, unless the actual text input is focused
    if (inputIsFocused.not()) {
      revertAllChanges()
    }
  }
  val resolvedInputTransformation = inputTransformation?.let {
    focusTransformation.then(it)
  } ?: focusTransformation

  BasicTextField(
    scrollState = scrollState,
    state = state,
    interactionSource = interactionSource,
    textStyle = newTextStyle,
    readOnly = editable.not(),
    outputTransformation = outputTransformation,
    inputTransformation = resolvedInputTransformation,
    modifier = modifier.semantics(mergeDescendants = true) {},
    cursorBrush = cursorBrush,
    lineLimits = lineLimits,
    onTextLayout = onTextLayout,
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
