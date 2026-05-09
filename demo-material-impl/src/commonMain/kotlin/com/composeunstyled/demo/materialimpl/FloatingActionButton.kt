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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Surface as M3Surface

private val FloatingActionButtonSize = 56.dp
private val SmallFloatingActionButtonSize = 40.dp
private val LargeFloatingActionButtonSize = 96.dp
private val FloatingActionButtonShadowElevation = 6.dp

@Composable
private fun FloatingActionButtonSurface(
  onClick: () -> Unit,
  modifier: Modifier,
  shape: Shape,
  containerColor: Color,
  contentColor: Color,
  size: Dp,
  elevation: FloatingActionButtonElevation,
  interactionSource: MutableInteractionSource?,
  content: @Composable () -> Unit,
) {
  M3Surface(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    color = containerColor,
    contentColor = contentColor,
    tonalElevation = FloatingActionButtonShadowElevation,
    shadowElevation = FloatingActionButtonShadowElevation,
    interactionSource = interactionSource,
  ) {
    ProvideTextStyle(MaterialTheme.typography.labelLarge) {
      Box(
        modifier = Modifier.defaultMinSize(minWidth = size, minHeight = size),
        contentAlignment = Alignment.Center,
      ) {
        content()
      }
    }
  }
}

@Composable
fun FloatingActionButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = FloatingActionButtonDefaults.shape,
  containerColor: Color = FloatingActionButtonDefaults.containerColor,
  contentColor: Color = androidx.compose.material3.contentColorFor(containerColor),
  elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  FloatingActionButtonSurface(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    size = FloatingActionButtonSize,
    elevation = elevation,
    interactionSource = interactionSource,
    content = content,
  )
}

@Composable
fun SmallFloatingActionButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = FloatingActionButtonDefaults.smallShape,
  containerColor: Color = FloatingActionButtonDefaults.containerColor,
  contentColor: Color = androidx.compose.material3.contentColorFor(containerColor),
  elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  FloatingActionButtonSurface(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    size = SmallFloatingActionButtonSize,
    elevation = elevation,
    interactionSource = interactionSource,
    content = content,
  )
}

@Composable
fun LargeFloatingActionButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = FloatingActionButtonDefaults.largeShape,
  containerColor: Color = FloatingActionButtonDefaults.containerColor,
  contentColor: Color = androidx.compose.material3.contentColorFor(containerColor),
  elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
  interactionSource: MutableInteractionSource? = null,
  content: @Composable () -> Unit,
) {
  FloatingActionButtonSurface(
    onClick = onClick,
    modifier = modifier,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    size = LargeFloatingActionButtonSize,
    elevation = elevation,
    interactionSource = interactionSource,
    content = content,
  )
}
