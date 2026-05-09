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
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@file:Suppress("unused", "UNUSED_PARAMETER")

package com.composeunstyled.demo.material3api

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.composeunstyled.TextInput
import com.composeunstyled.UnstyledTextField
import com.composeunstyled.buildModifier

private val TextFieldContainerHeight = 56.dp
private val TextFieldHorizontalPadding = 16.dp
private val TextFieldIconHorizontalPadding = 12.dp
private val TextFieldIconSize = 24.dp
private val TextFieldLabelCollapsedTopPadding = 8.dp
private val TextFieldLabelExpandedTopPadding = 18.dp
private val TextFieldLabelScale = 0.75f
private val TextFieldFocusedIndicatorWidth = 2.dp
private val TextFieldUnfocusedIndicatorWidth = 1.dp
private val OutlinedTextFieldLabelCutoutPadding = 4.dp
private val TextFieldLabelAnimationSpec = tween<Float>(
  durationMillis = 150,
  easing = CubicBezierEasing(0.2f, 0f, 0f, 1f),
)
private enum class TextFieldVariant {
  Filled,
  Outlined,
}

private fun Modifier.outlinedTextFieldCutout(
  labelSize: Size,
  labelStartPadding: Dp,
): Modifier = drawWithContent {
  if (labelSize.width > 0f) {
    val cutoutPadding = OutlinedTextFieldLabelCutoutPadding.toPx()
    val leftPadding = when (layoutDirection) {
      LayoutDirection.Ltr -> labelStartPadding.toPx()
      LayoutDirection.Rtl -> size.width - labelStartPadding.toPx() - labelSize.width
    }
    val left = (leftPadding - cutoutPadding).coerceAtLeast(0f)
    val right = (leftPadding + labelSize.width + cutoutPadding).coerceAtMost(size.width)
    val cutoutHeight = labelSize.height

    clipRect(left, -cutoutHeight / 2f, right, cutoutHeight / 2f, ClipOp.Difference) {
      this@drawWithContent.drawContent()
    }
  } else {
    drawContent()
  }
}

@Composable
fun TextField(
  state: TextFieldState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  textStyle: TextStyle = LocalTextStyle.current,
  label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  prefix: @Composable (() -> Unit)? = null,
  suffix: @Composable (() -> Unit)? = null,
  supportingText: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  inputTransformation: InputTransformation? = null,
  outputTransformation: OutputTransformation? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  onKeyboardAction: KeyboardActionHandler? = null,
  lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
  onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
  scrollState: ScrollState = rememberScrollState(),
  interactionSource: MutableInteractionSource? = null,
  shape: Shape = TextFieldDefaults.shape,
  colors: TextFieldColors = TextFieldDefaults.colors(),
) {
  TextFieldContent(
    state = state,
    modifier = modifier,
    enabled = enabled,
    readOnly = readOnly,
    textStyle = textStyle,
    label = label,
    placeholder = placeholder,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    prefix = prefix,
    suffix = suffix,
    supportingText = supportingText,
    isError = isError,
    inputTransformation = inputTransformation,
    outputTransformation = outputTransformation,
    keyboardOptions = keyboardOptions,
    onKeyboardAction = onKeyboardAction,
    lineLimits = lineLimits,
    onTextLayout = onTextLayout,
    scrollState = scrollState,
    shape = shape,
    variant = TextFieldVariant.Filled,
    interactionSource = interactionSource,
  )
}

@Composable
private fun TextFieldContent(
  state: TextFieldState,
  modifier: Modifier,
  enabled: Boolean,
  readOnly: Boolean,
  textStyle: TextStyle,
  label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  prefix: @Composable (() -> Unit)? = null,
  suffix: @Composable (() -> Unit)? = null,
  supportingText: @Composable (() -> Unit)? = null,
  isError: Boolean,
  inputTransformation: InputTransformation?,
  outputTransformation: OutputTransformation?,
  keyboardOptions: KeyboardOptions,
  onKeyboardAction: KeyboardActionHandler? = null,
  lineLimits: TextFieldLineLimits,
  onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)?,
  scrollState: ScrollState,
  shape: Shape,
  variant: TextFieldVariant,
  interactionSource: MutableInteractionSource? = null,
) {
  val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
  val isFocused by resolvedInteractionSource.collectIsFocusedAsState()
  val isHovered by resolvedInteractionSource.collectIsHoveredAsState()
  val labelMinimized = label != null && (isFocused || state.text.isNotEmpty())
  var labelSize by remember { mutableStateOf(IntSize.Zero) }
  val labelTransition = updateTransition(labelMinimized, label = "TextFieldLabel")
  val labelProgress by labelTransition.animateFloat(
    transitionSpec = { TextFieldLabelAnimationSpec },
    label = "TextFieldLabelProgress",
  ) { minimized ->
    if (minimized) 1f else 0f
  }

  val activeColor = when {
    enabled.not() -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    isError -> MaterialTheme.colorScheme.error
    isFocused -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.onSurfaceVariant
  }
  val indicatorColor = when {
    enabled.not() -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    isError -> MaterialTheme.colorScheme.error
    isFocused -> MaterialTheme.colorScheme.primary
    isHovered -> MaterialTheme.colorScheme.onSurface
    variant == TextFieldVariant.Outlined -> MaterialTheme.colorScheme.outline
    else -> MaterialTheme.colorScheme.onSurfaceVariant
  }
  val containerColor = when (variant) {
    TextFieldVariant.Filled -> if (enabled) {
      MaterialTheme.colorScheme.surfaceContainerHighest
    } else {
      MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
    }
    TextFieldVariant.Outlined -> Color.Transparent
  }
  val indicatorWidth = if (isFocused) {
    TextFieldFocusedIndicatorWidth
  } else {
    TextFieldUnfocusedIndicatorWidth
  }
  val horizontalPadding = if (leadingIcon == null) {
    TextFieldHorizontalPadding
  } else {
    TextFieldIconHorizontalPadding
  }
  val labelStartPadding = if (leadingIcon == null) {
    TextFieldHorizontalPadding
  } else {
    TextFieldIconHorizontalPadding + TextFieldIconSize + TextFieldHorizontalPadding
  }
  val labelScale = lerp(1f, TextFieldLabelScale, labelProgress)
  val outlinedCutoutLabelSize = if (variant == TextFieldVariant.Outlined && labelProgress > 0f) {
    Size(
      width = labelSize.width * labelScale * labelProgress,
      height = labelSize.height * labelScale,
    )
  } else {
    Size.Zero
  }
  val labelTopPadding = when (variant) {
    TextFieldVariant.Filled -> lerp(
      TextFieldLabelExpandedTopPadding.value,
      TextFieldLabelCollapsedTopPadding.value,
      labelProgress,
    ).dp
    TextFieldVariant.Outlined -> lerp(16f, (-7).toFloat(), labelProgress).dp
  }
  val inputTopPadding = if (label == null) {
    8.dp
  } else {
    when (variant) {
      TextFieldVariant.Filled -> 24.dp
      TextFieldVariant.Outlined -> 16.dp
    }
  }
  val inputBottomPadding = if (label == null) 8.dp else 8.dp

  Column(modifier) {
    UnstyledTextField(
      state = state,
      editable = enabled && readOnly.not(),
      textStyle = textStyle,
      textColor = MaterialTheme.colorScheme.onSurface,
      keyboardOptions = keyboardOptions,
      onKeyboardAction = onKeyboardAction,
      inputTransformation = inputTransformation,
      outputTransformation = outputTransformation,
      lineLimits = lineLimits,
      onTextLayout = onTextLayout,
      interactionSource = resolvedInteractionSource,
      scrollState = scrollState,
    ) {
      Box(Modifier.fillMaxWidth()) {
        TextInput(
          modifier = (
            Modifier
              .fillMaxWidth()
              .heightIn(min = TextFieldContainerHeight)
              .clip(shape)
              .background(containerColor, shape)
            ) then buildModifier {
            when (variant) {
              TextFieldVariant.Filled -> Unit
              TextFieldVariant.Outlined ->
                add(
                  Modifier
                    .outlinedTextFieldCutout(outlinedCutoutLabelSize, labelStartPadding)
                    .border(indicatorWidth, indicatorColor, shape),
                )
            }
          },
          contentPadding = PaddingValues(
            start = horizontalPadding,
            top = inputTopPadding,
            end = TextFieldHorizontalPadding,
            bottom = inputBottomPadding,
          ),
          placeholder = if (label == null || labelMinimized) placeholder else null,
          leading = if (leadingIcon != null || prefix != null) {
            {
              Row(verticalAlignment = Alignment.CenterVertically) {
                leadingIcon?.let {
                  Box(
                    Modifier
                      .padding(end = TextFieldHorizontalPadding)
                      .size(TextFieldIconSize),
                    contentAlignment = Alignment.Center,
                  ) {
                    CompositionLocalProvider(LocalContentColor provides activeColor) {
                      it()
                    }
                  }
                }
                prefix?.let {
                  CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                  ) {
                    it()
                  }
                }
              }
            }
          } else {
            null
          },
          trailing = if (suffix != null || trailingIcon != null) {
            {
              Row(verticalAlignment = Alignment.CenterVertically) {
                suffix?.let {
                  CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                  ) {
                    it()
                  }
                }
                trailingIcon?.let {
                  Box(
                    Modifier
                      .padding(start = TextFieldHorizontalPadding)
                      .size(TextFieldIconSize),
                    contentAlignment = Alignment.Center,
                  ) {
                    CompositionLocalProvider(LocalContentColor provides activeColor) {
                      it()
                    }
                  }
                }
              }
            }
          } else {
            null
          },
        )
        if (variant == TextFieldVariant.Filled) {
          Box(
            Modifier
              .align(Alignment.BottomStart)
              .fillMaxWidth()
              .height(indicatorWidth)
              .background(indicatorColor),
          )
        }
        label?.let {
          Box(
            Modifier
              .padding(start = labelStartPadding)
              .offset(y = labelTopPadding)
              .onSizeChanged { labelSize = it }
              .graphicsLayer {
                scaleX = labelScale
                scaleY = labelScale
                transformOrigin = TransformOrigin(0f, 0f)
              },
          ) {
            CompositionLocalProvider(LocalContentColor provides activeColor) {
              ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                with(
                  object : TextFieldLabelScope {
                    override val labelMinimizedProgress: Float = labelProgress
                  },
                ) {
                  it()
                }
              }
            }
          }
        }
      }
    }
    supportingText?.let {
      Box(Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp)) {
        CompositionLocalProvider(
          LocalContentColor provides if (isError) {
            MaterialTheme.colorScheme.error
          } else {
            MaterialTheme.colorScheme.onSurfaceVariant
          },
        ) {
          ProvideTextStyle(MaterialTheme.typography.bodySmall) {
            it()
          }
        }
      }
    }
  }
}

@Composable
fun OutlinedTextField(
  state: TextFieldState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  textStyle: TextStyle = LocalTextStyle.current,
  label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  prefix: @Composable (() -> Unit)? = null,
  suffix: @Composable (() -> Unit)? = null,
  supportingText: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  inputTransformation: InputTransformation? = null,
  outputTransformation: OutputTransformation? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  onKeyboardAction: KeyboardActionHandler? = null,
  lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
  onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
  scrollState: ScrollState = rememberScrollState(),
  interactionSource: MutableInteractionSource? = null,
  shape: Shape = OutlinedTextFieldDefaults.shape,
  colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
  TextFieldContent(
    state = state,
    modifier = modifier,
    enabled = enabled,
    readOnly = readOnly,
    textStyle = textStyle,
    label = label,
    placeholder = placeholder,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    prefix = prefix,
    suffix = suffix,
    supportingText = supportingText,
    isError = isError,
    keyboardOptions = keyboardOptions,
    inputTransformation = inputTransformation,
    outputTransformation = outputTransformation,
    onKeyboardAction = onKeyboardAction,
    lineLimits = lineLimits,
    onTextLayout = onTextLayout,
    scrollState = scrollState,
    shape = shape,
    variant = TextFieldVariant.Outlined,
    interactionSource = interactionSource,
  )
}
