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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.composeunstyled.AnchorAlignment
import com.composeunstyled.AnchorSide
import com.composeunstyled.TooltipPanel
import com.composeunstyled.UnstyledTooltip

interface TooltipScope : com.composeunstyled.TooltipScope

private object MaterialTooltipScope : TooltipScope

@Composable
fun TooltipBox(
  tooltip: @Composable TooltipScope.() -> Unit,
  modifier: Modifier = Modifier,
  enableUserInput: Boolean = true,
  content: @Composable () -> Unit,
) {
  val scope = remember { MaterialTooltipScope }

  Box(modifier = modifier) {
    UnstyledTooltip(
      enabled = enableUserInput,
      side = AnchorSide.Top,
      alignment = AnchorAlignment.Center,
      panel = { scope.tooltip() },
      anchor = content,
    )
  }
}

@Composable
fun TooltipScope.PlainTooltip(
  modifier: Modifier = Modifier,
  caretShape: Shape? = null,
  maxWidth: Dp = TooltipDefaults.plainTooltipMaxWidth,
  shape: Shape = TooltipDefaults.plainTooltipContainerShape,
  contentColor: Color = TooltipDefaults.plainTooltipContentColor,
  containerColor: Color = TooltipDefaults.plainTooltipContainerColor,
  tonalElevation: Dp = 0.dp,
  shadowElevation: Dp = 0.dp,
  content: @Composable () -> Unit,
) {
  TooltipPanel(
    modifier = modifier
      .zIndex(1f)
      .offset(y = (-4).dp),
    enter = fadeIn(animationSpec = tween(durationMillis = 150)) +
      scaleIn(
        animationSpec = tween(durationMillis = 150),
        initialScale = 0.8f,
        transformOrigin = TransformOrigin(0.5f, 1f),
      ),
    exit = fadeOut(animationSpec = tween(durationMillis = 75)) +
      scaleOut(
        animationSpec = tween(durationMillis = 75),
        targetScale = 0.8f,
        transformOrigin = TransformOrigin(0.5f, 1f),
      ),
  ) {
    Box(
      Modifier
        .sizeIn(
          minWidth = 40.dp,
          maxWidth = maxWidth,
          minHeight = 24.dp,
        )
        .background(containerColor, shape)
        .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
      CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides MaterialTheme.typography.bodySmall,
      ) {
        content()
      }
    }
  }
}
