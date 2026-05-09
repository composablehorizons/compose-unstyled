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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledButton
import com.composeunstyled.buildModifier

private val ButtonMinWidth = 58.dp
private val ButtonMinHeight = 40.dp

@Composable
private fun ButtonSurface(
  onClick: () -> Unit,
  modifier: Modifier,
  enabled: Boolean,
  shape: Shape,
  backgroundColor: Color,
  contentColor: Color,
  border: BorderStroke?,
  contentPadding: PaddingValues,
  interactionSource: MutableInteractionSource?,
  shadowElevation: Dp = 0.dp,
  minWidth: Dp = ButtonMinWidth,
  minHeight: Dp = ButtonMinHeight,
  content: @Composable RowScope.() -> Unit,
) {
  val resolvedModifier = modifier.shadow(shadowElevation, shape)
  CompositionLocalProvider(LocalContentColor provides contentColor) {
    ProvideTextStyle(MaterialTheme.typography.labelLarge) {
      UnstyledButton(
        onClick = onClick,
        enabled = enabled,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        modifier = (
          resolvedModifier
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
            .clip(shape)
            .background(backgroundColor, shape)
          ) then buildModifier {
          if (border != null) {
            add(Modifier.border(border, shape))
          }
        },
      ) {
        Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
          content = content,
        )
      }
    }
  }
}

@Composable
fun AssistChip(
  onClick: () -> Unit,
  label: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  shape: Shape = AssistChipDefaults.shape,
  colors: androidx.compose.material3.ChipColors = AssistChipDefaults.assistChipColors(),
  elevation: androidx.compose.material3.ChipElevation? = AssistChipDefaults.assistChipElevation(),
  border: BorderStroke? = AssistChipDefaults.assistChipBorder(enabled),
  interactionSource: MutableInteractionSource? = null,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    backgroundColor = Color.Transparent,
    contentColor = MaterialTheme.colorScheme.onSurface,
    border = border,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    interactionSource = interactionSource,
  ) {
    leadingIcon?.invoke()
    label()
    trailingIcon?.invoke()
  }
}

@Composable
fun FilterChip(
  selected: Boolean,
  onClick: () -> Unit,
  label: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  shape: Shape = androidx.compose.material3.FilterChipDefaults.shape,
  colors: SelectableChipColors =
    androidx.compose.material3.FilterChipDefaults.filterChipColors(),
  elevation: SelectableChipElevation? =
    androidx.compose.material3.FilterChipDefaults.filterChipElevation(),
  border: BorderStroke? =
    androidx.compose.material3.FilterChipDefaults.filterChipBorder(enabled, selected),
  interactionSource: MutableInteractionSource? = null,
) {
  AssistChip(
    onClick = onClick,
    label = label,
    modifier = modifier,
    enabled = enabled,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    shape = shape,
    border = border,
    interactionSource = interactionSource,
  )
}

@Composable
fun InputChip(
  selected: Boolean,
  onClick: () -> Unit,
  label: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  avatar: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  shape: Shape = InputChipDefaults.shape,
  colors: SelectableChipColors = InputChipDefaults.inputChipColors(),
  elevation: SelectableChipElevation? = InputChipDefaults.inputChipElevation(),
  border: BorderStroke? = InputChipDefaults.inputChipBorder(enabled, selected),
  interactionSource: MutableInteractionSource? = null,
) {
  AssistChip(
    onClick = onClick,
    label = label,
    modifier = modifier,
    enabled = enabled,
    leadingIcon = avatar ?: leadingIcon,
    trailingIcon = trailingIcon,
    shape = shape,
    border = border,
    interactionSource = interactionSource,
  )
}

@Composable
fun SuggestionChip(
  onClick: () -> Unit,
  label: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  icon: @Composable (() -> Unit)? = null,
  shape: Shape = SuggestionChipDefaults.shape,
  colors: androidx.compose.material3.ChipColors = SuggestionChipDefaults.suggestionChipColors(),
  elevation: androidx.compose.material3.ChipElevation? =
    SuggestionChipDefaults.suggestionChipElevation(),
  border: BorderStroke? = SuggestionChipDefaults.suggestionChipBorder(enabled),
  interactionSource: MutableInteractionSource? = null,
) {
  AssistChip(
    onClick = onClick,
    label = label,
    modifier = modifier,
    enabled = enabled,
    leadingIcon = icon,
    shape = shape,
    border = border,
    interactionSource = interactionSource,
  )
}
