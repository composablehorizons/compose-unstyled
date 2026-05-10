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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButtonColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.minimumInteractiveComponentSize
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
import com.composeunstyled.UnstyledSwitch
import com.composeunstyled.buildModifier

private val IconButtonSize = 40.dp
private val ButtonMinWidth = 58.dp
private val ButtonMinHeight = 40.dp
private fun IconButtonColors.containerColorFor(enabled: Boolean): Color =
  if (enabled) containerColor else disabledContainerColor

private fun IconButtonColors.contentColorFor(enabled: Boolean): Color =
  if (enabled) contentColor else disabledContentColor

private fun IconToggleButtonColors.containerColorFor(enabled: Boolean, checked: Boolean): Color =
  when {
    enabled && checked -> checkedContainerColor
    enabled && checked.not() -> containerColor
    else -> disabledContainerColor
  }

private fun IconToggleButtonColors.contentColorFor(enabled: Boolean, checked: Boolean): Color =
  when {
    enabled && checked -> checkedContentColor
    enabled && checked.not() -> contentColor
    else -> disabledContentColor
  }

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
          modifier = Modifier.padding(contentPadding),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
          content = content,
        )
      }
    }
  }
}

@Composable
fun IconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier.minimumInteractiveComponentSize().size(IconButtonSize),
    enabled = enabled,
    shape = MaterialTheme.shapes.extraLarge,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = null,
    contentPadding = PaddingValues(0.dp),
    interactionSource = interactionSource,
    minWidth = 0.dp,
    minHeight = 0.dp,
  ) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
  }
}

@Composable
fun IconToggleButton(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: IconToggleButtonColors = IconButtonDefaults.iconToggleButtonColors(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  UnstyledSwitch(
    checked = checked,
    onCheckedChange = onCheckedChange,
    enabled = enabled,
    interactionSource = interactionSource,
    modifier = modifier
      .minimumInteractiveComponentSize()
      .size(IconButtonSize)
      .clip(MaterialTheme.shapes.extraLarge)
      .background(
        colors.containerColorFor(enabled, checked),
        MaterialTheme.shapes.extraLarge,
      ),
  ) {
    CompositionLocalProvider(LocalContentColor provides colors.contentColorFor(enabled, checked)) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
    }
  }
}

@Composable
fun FilledIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = IconButtonDefaults.filledShape,
  colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier.minimumInteractiveComponentSize().size(IconButtonSize),
    enabled = enabled,
    shape = shape,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = null,
    contentPadding = PaddingValues(0.dp),
    interactionSource = interactionSource,
    minWidth = 0.dp,
    minHeight = 0.dp,
  ) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
  }
}

@Composable
fun FilledTonalIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = IconButtonDefaults.filledShape,
  colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier.minimumInteractiveComponentSize().size(IconButtonSize),
    enabled = enabled,
    shape = shape,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = null,
    contentPadding = PaddingValues(0.dp),
    interactionSource = interactionSource,
    minWidth = 0.dp,
    minHeight = 0.dp,
  ) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
  }
}

@Composable
fun OutlinedIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = IconButtonDefaults.outlinedShape,
  colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
  border: BorderStroke? = IconButtonDefaults.outlinedIconButtonBorder(enabled),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  ButtonSurface(
    onClick = onClick,
    modifier = modifier.minimumInteractiveComponentSize().size(IconButtonSize),
    enabled = enabled,
    shape = shape,
    backgroundColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = border,
    contentPadding = PaddingValues(0.dp),
    interactionSource = interactionSource,
    minWidth = 0.dp,
    minHeight = 0.dp,
  ) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
  }
}
