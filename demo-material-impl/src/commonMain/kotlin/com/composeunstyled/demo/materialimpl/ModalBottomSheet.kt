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

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.composeunstyled.ModalBottomSheetState
import com.composeunstyled.ModalSheetProperties
import com.composeunstyled.Scrim
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.UnstyledModalBottomSheet
import com.composeunstyled.currentWindowContainerSize
import com.composeunstyled.rememberModalBottomSheetState
import kotlin.math.max
import androidx.compose.material3.Surface as M3Surface

private val ModalBottomSheetTopMargin = 72.dp
private val ModalBottomSheetWideTopMargin = 56.dp
private val ModalBottomSheetWideSideMargin = 56.dp
private const val ModalBottomSheetAnimationDurationMillis = 300

@Composable
fun ModalBottomSheet(
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  sheetState: ModalBottomSheetState = rememberModalBottomSheetState(
    initialDetent = SheetDetent.Hidden,
    animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
    dismissAnimationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
  ),
  sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
  sheetGesturesEnabled: Boolean = true,
  shape: Shape = BottomSheetDefaults.ExpandedShape,
  containerColor: Color = BottomSheetDefaults.ContainerColor,
  contentColor: Color = androidx.compose.material3.contentColorFor(containerColor),
  tonalElevation: Dp = 0.dp,
  scrimColor: Color = BottomSheetDefaults.ScrimColor,
  dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
  contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
  properties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
  content: @Composable ColumnScope.() -> Unit,
) {
  UnstyledModalBottomSheet(
    state = sheetState,
    properties = ModalSheetProperties(
      dismissOnBackPress = properties.shouldDismissOnBackPress,
      dismissOnClickOutside = true,
    ),
    onDismiss = onDismissRequest,
    overlay = {
      Scrim(
        scrimColor = scrimColor,
        enter = fadeIn(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
        exit = fadeOut(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
      )
    },
  ) {
    CompositionLocalProvider(LocalContentColor provides contentColor) {
      val windowSize = currentWindowContainerSize()
      val windowWidth = windowSize.width
      val useWideWindowMargins = sheetMaxWidth.isSpecified && windowWidth > sheetMaxWidth
      val topMargin = if (useWideWindowMargins) {
        ModalBottomSheetWideTopMargin
      } else {
        ModalBottomSheetTopMargin
      }
      val maxSheetHeight = (windowSize.height - topMargin).coerceAtLeast(0.dp)
      val sideMargin = if (useWideWindowMargins) {
        ModalBottomSheetWideSideMargin
      } else {
        0.dp
      }
      Sheet(
        modifier = modifier
          .padding(
            start = sideMargin,
            end = sideMargin,
          )
          .heightIn(max = maxSheetHeight)
          .widthIn(max = sheetMaxWidth)
          .fillMaxWidth(),
      ) {
        M3Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = shape,
          color = containerColor,
          contentColor = contentColor,
          tonalElevation = tonalElevation,
        ) {
          Column(Modifier.fillMaxWidth().windowInsetsPadding(contentWindowInsets())) {
            dragHandle?.let {
              Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                it()
              }
            }
            content()
          }
        }
      }
    }
  }
}
