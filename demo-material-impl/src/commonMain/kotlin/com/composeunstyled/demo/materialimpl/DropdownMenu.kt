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

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.composeunstyled.DropdownMenuPanel
import com.composeunstyled.UnstyledDropdownMenu
import com.composeunstyled.buildModifier

private const val DropdownMenuEnterDurationMillis = 120
private const val DropdownMenuExitDurationMillis = 75
private const val DropdownMenuFadeInDurationMillis = 30
private const val DropdownMenuInitialScale = 0.8f
private const val DropdownMenuTargetScale = 0.95f
private val DropdownMenuTransformOrigin = TransformOrigin(0.5f, 0f)
private val DropdownMenuEnterTransition = fadeIn(
  animationSpec = tween(DropdownMenuFadeInDurationMillis),
) + scaleIn(
  initialScale = DropdownMenuInitialScale,
  transformOrigin = DropdownMenuTransformOrigin,
  animationSpec = tween(DropdownMenuEnterDurationMillis),
)

private val DropdownMenuExitTransition = fadeOut(
  animationSpec = tween(DropdownMenuExitDurationMillis),
) + scaleOut(
  targetScale = DropdownMenuTargetScale,
  transformOrigin = DropdownMenuTransformOrigin,
  animationSpec = tween(DropdownMenuExitDurationMillis),
)

@Composable
fun DropdownMenuItem(
  text: @Composable () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  enabled: Boolean = true,
  colors: androidx.compose.material3.MenuItemColors = MenuDefaults.itemColors(),
  contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
  interactionSource: MutableInteractionSource? = null,
) {
  Row(
    modifier = modifier
      .clickable(
        enabled = enabled,
        onClick = onClick,
        interactionSource = interactionSource,
        indication = ripple(),
      )
      .fillMaxWidth()
      .defaultMinSize(minHeight = 48.dp)
      .padding(contentPadding),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    ProvideTextStyle(MaterialTheme.typography.labelLarge) {
      if (leadingIcon != null) {
        val leadingIconColor = if (enabled) {
          colors.leadingIconColor
        } else {
          colors.disabledLeadingIconColor
        }
        CompositionLocalProvider(LocalContentColor provides leadingIconColor) {
          Box(Modifier.defaultMinSize(minWidth = 24.dp), contentAlignment = Alignment.CenterStart) {
            leadingIcon()
          }
        }
      }
      val textColor = if (enabled) colors.textColor else colors.disabledTextColor
      CompositionLocalProvider(LocalContentColor provides textColor) {
        Box(
          Modifier
            .weight(1f)
            .padding(
              start = if (leadingIcon != null) 12.dp else 0.dp,
              end = if (trailingIcon != null) 12.dp else 0.dp,
            ),
        ) {
          text()
        }
      }
      if (trailingIcon != null) {
        val trailingIconColor = if (enabled) {
          colors.trailingIconColor
        } else {
          colors.disabledTrailingIconColor
        }
        CompositionLocalProvider(LocalContentColor provides trailingIconColor) {
          Box(Modifier.defaultMinSize(minWidth = 24.dp), contentAlignment = Alignment.CenterEnd) {
            trailingIcon()
          }
        }
      }
    }
  }
}

@Composable
fun DropdownMenu(
  expanded: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  offset: DpOffset = DpOffset(0.dp, 0.dp),
  scrollState: ScrollState = rememberScrollState(),
  shape: Shape = MenuDefaults.shape,
  containerColor: Color = MenuDefaults.containerColor,
  shadowElevation: Dp = MenuDefaults.ShadowElevation,
  border: BorderStroke? = null,
  anchor: @Composable () -> Unit,
  content: @Composable ColumnScope.() -> Unit,
) {
  UnstyledDropdownMenu(
    expanded = expanded,
    onExpandedChange = onExpandedChange,
    sideOffset = offset.y,
    alignmentOffset = offset.x,
    panel = {
      DropdownMenuPanel(
        modifier = (
          modifier.width(IntrinsicSize.Max)
            .graphicsLayer {
              this.shadowElevation = shadowElevation.toPx()
              this.shape = shape
              clip = false
            }
            .clip(shape)
            .background(containerColor)
          ) then buildModifier {
          if (border != null) {
            add(Modifier.border(border, shape))
          }
          add(Modifier.verticalScroll(scrollState))
          add(Modifier.padding(vertical = 8.dp))
        },
        enter = DropdownMenuEnterTransition,
        exit = DropdownMenuExitTransition,
      ) {
        Column(content = content)
      }
    },
    anchor = anchor,
  )
}

@Composable
fun ExposedDropdownMenu(
  expanded: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  scrollState: ScrollState = rememberScrollState(),
  shape: Shape = MenuDefaults.shape,
  containerColor: Color = MenuDefaults.containerColor,
  shadowElevation: Dp = MenuDefaults.ShadowElevation,
  border: BorderStroke? = null,
  anchor: @Composable () -> Unit,
  content: @Composable ColumnScope.() -> Unit,
) {
  DropdownMenu(
    expanded = expanded,
    onExpandedChange = onExpandedChange,
    modifier = modifier,
    scrollState = scrollState,
    shape = shape,
    containerColor = containerColor,
    shadowElevation = shadowElevation,
    border = border,
    anchor = anchor,
    content = content,
  )
}
