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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.buildModifier

private fun CardColors.containerColorFor(enabled: Boolean): Color =
  if (enabled) containerColor else disabledContainerColor

private fun CardColors.contentColorFor(enabled: Boolean): Color =
  if (enabled) contentColor else disabledContentColor

@Composable
private fun CardSurface(
  modifier: Modifier,
  shape: Shape,
  containerColor: Color,
  contentColor: Color,
  border: BorderStroke?,
  onClick: (() -> Unit)? = null,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  shadowElevation: Dp = 0.dp,
  content: @Composable ColumnScope.() -> Unit,
) {
  val cardInteractionSource = if (onClick != null) {
    interactionSource ?: remember { MutableInteractionSource() }
  } else {
    null
  }
  CompositionLocalProvider(LocalContentColor provides contentColor) {
    Box(
      modifier = (
        modifier
          .shadow(shadowElevation, shape)
          .clip(shape)
          .background(containerColor, shape)
        ) then buildModifier {
        if (border != null) {
          add(Modifier.border(border, shape))
        }
        if (onClick != null) {
          add(
            Modifier.clickable(
              enabled = enabled,
              interactionSource = cardInteractionSource,
              indication = ripple(),
              onClick = onClick,
            ),
          )
        }
      },
    ) {
      Column(content = content)
    }
  }
}

@Composable
fun Card(
  modifier: Modifier = Modifier,
  shape: Shape = CardDefaults.shape,
  colors: CardColors = CardDefaults.cardColors(),
  elevation: CardElevation = CardDefaults.cardElevation(),
  border: BorderStroke? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  CardSurface(
    modifier = modifier,
    shape = shape,
    containerColor = colors.containerColorFor(enabled = true),
    contentColor = colors.contentColorFor(enabled = true),
    border = border,
    content = content,
  )
}

@Composable
fun Card(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = CardDefaults.shape,
  colors: CardColors = CardDefaults.cardColors(),
  elevation: CardElevation = CardDefaults.cardElevation(),
  border: BorderStroke? = null,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  CardSurface(
    modifier = modifier,
    shape = shape,
    containerColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = border,
    onClick = onClick,
    enabled = enabled,
    interactionSource = interactionSource,
    content = content,
  )
}

@Composable
fun ElevatedCard(
  modifier: Modifier = Modifier,
  shape: Shape = CardDefaults.elevatedShape,
  colors: CardColors = CardDefaults.elevatedCardColors(),
  elevation: CardElevation = CardDefaults.elevatedCardElevation(),
  content: @Composable ColumnScope.() -> Unit,
) {
  CardSurface(
    modifier = modifier,
    shape = shape,
    containerColor = colors.containerColorFor(enabled = true),
    contentColor = colors.contentColorFor(enabled = true),
    border = null,
    shadowElevation = 1.dp,
    content = content,
  )
}

@Composable
fun ElevatedCard(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = CardDefaults.elevatedShape,
  colors: CardColors = CardDefaults.elevatedCardColors(),
  elevation: CardElevation = CardDefaults.elevatedCardElevation(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  CardSurface(
    modifier = modifier,
    shape = shape,
    containerColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = null,
    onClick = onClick,
    enabled = enabled,
    interactionSource = interactionSource,
    shadowElevation = if (enabled) 1.dp else 0.dp,
    content = content,
  )
}

@Composable
fun OutlinedCard(
  modifier: Modifier = Modifier,
  shape: Shape = CardDefaults.outlinedShape,
  colors: CardColors = CardDefaults.outlinedCardColors(),
  elevation: CardElevation = CardDefaults.outlinedCardElevation(),
  border: BorderStroke = CardDefaults.outlinedCardBorder(),
  content: @Composable ColumnScope.() -> Unit,
) {
  CardSurface(
    modifier = modifier,
    shape = shape,
    containerColor = colors.containerColorFor(enabled = true),
    contentColor = colors.contentColorFor(enabled = true),
    border = border,
    content = content,
  )
}

@Composable
fun OutlinedCard(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = CardDefaults.outlinedShape,
  colors: CardColors = CardDefaults.outlinedCardColors(),
  elevation: CardElevation = CardDefaults.outlinedCardElevation(),
  border: BorderStroke = CardDefaults.outlinedCardBorder(enabled),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  CardSurface(
    modifier = modifier,
    shape = shape,
    containerColor = colors.containerColorFor(enabled),
    contentColor = colors.contentColorFor(enabled),
    border = border,
    onClick = onClick,
    enabled = enabled,
    interactionSource = interactionSource,
    content = content,
  )
}
