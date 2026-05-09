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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledSwitch

private val SwitchWidth = 52.dp
private val SwitchHeight = 32.dp
private val SwitchThumbSize = 24.dp
private val SwitchUncheckedThumbSize = 16.dp
private val SwitchPressedThumbSize = 28.dp
private val SwitchStateLayerSize = 40.dp
private val SwitchTrackOutlineWidth = 2.dp
private fun SwitchColors.trackColorFor(enabled: Boolean, checked: Boolean): Color =
  when {
    enabled && checked -> checkedTrackColor
    enabled && checked.not() -> uncheckedTrackColor
    enabled.not() && checked -> disabledCheckedTrackColor
    else -> disabledUncheckedTrackColor
  }

private fun SwitchColors.thumbColorFor(enabled: Boolean, checked: Boolean): Color =
  when {
    enabled && checked -> checkedThumbColor
    enabled && checked.not() -> uncheckedThumbColor
    enabled.not() && checked -> disabledCheckedThumbColor
    else -> disabledUncheckedThumbColor
  }

private fun SwitchColors.borderColorFor(enabled: Boolean, checked: Boolean): Color =
  when {
    enabled && checked -> checkedBorderColor
    enabled && checked.not() -> uncheckedBorderColor
    enabled.not() && checked -> disabledCheckedBorderColor
    else -> disabledUncheckedBorderColor
  }

private fun SwitchColors.iconColorFor(enabled: Boolean, checked: Boolean): Color =
  when {
    enabled && checked -> checkedIconColor
    enabled && checked.not() -> uncheckedIconColor
    enabled.not() && checked -> disabledCheckedIconColor
    else -> disabledUncheckedIconColor
  }
private fun Modifier.minimumInteractiveComponentSize(enabled: Boolean): Modifier =
  if (enabled) {
    minimumInteractiveComponentSize()
  } else {
    this
  }

@Composable
fun Switch(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit)?,
  modifier: Modifier = Modifier,
  thumbContent: (@Composable () -> Unit)? = null,
  enabled: Boolean = true,
  colors: SwitchColors = SwitchDefaults.colors(),
  interactionSource: MutableInteractionSource? = null,
) {
  val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
  val pressed by resolvedInteractionSource.collectIsPressedAsState()
  val thumbTargetSize = when {
    pressed -> SwitchPressedThumbSize
    checked || thumbContent != null -> SwitchThumbSize
    else -> SwitchUncheckedThumbSize
  }
  val thumbMotionSpec = if (pressed) {
    snap<Dp>()
  } else {
    MaterialTheme.motionScheme.fastSpatialSpec()
  }
  val thumbSize by animateDpAsState(
    targetValue = thumbTargetSize,
    animationSpec = thumbMotionSpec,
  )
  val checkedThumbOffset = SwitchWidth - SwitchThumbSize - (SwitchHeight - SwitchThumbSize) / 2
  val thumbOffsetTarget = when {
    pressed && checked -> checkedThumbOffset - SwitchTrackOutlineWidth
    pressed && checked.not() -> SwitchTrackOutlineWidth
    checked -> checkedThumbOffset
    else -> (SwitchHeight - thumbTargetSize) / 2
  }
  val thumbOffset by animateDpAsState(
    targetValue = thumbOffsetTarget,
    animationSpec = thumbMotionSpec,
  )
  val trackShape = MaterialTheme.shapes.extraLarge

  UnstyledSwitch(
    checked = checked,
    onCheckedChange = onCheckedChange,
    enabled = enabled,
    interactionSource = resolvedInteractionSource,
    indication = null,
    modifier = modifier
      .minimumInteractiveComponentSize(onCheckedChange != null)
      .wrapContentSize(Alignment.Center)
      .size(SwitchWidth, SwitchHeight),
  ) {
    Box(
      Modifier.fillMaxSize(),
    ) {
      Box(
        Modifier
          .matchParentSize()
          .background(colors.trackColorFor(enabled, checked), trackShape)
          .border(SwitchTrackOutlineWidth, colors.borderColorFor(enabled, checked), trackShape),
      )

      Box(
        Modifier
          .align(Alignment.CenterStart)
          .offset(x = thumbOffset)
          .size(thumbSize)
          .indication(
            interactionSource = resolvedInteractionSource,
            indication = ripple(
              bounded = false,
              radius = SwitchStateLayerSize / 2,
            ),
          )
          .background(
            colors.thumbColorFor(enabled, checked),
            trackShape,
          ),
        contentAlignment = Alignment.Center,
      ) {
        CompositionLocalProvider(
          LocalContentColor provides colors.iconColorFor(enabled, checked),
        ) {
          thumbContent?.invoke()
        }
      }
    }
  }
}
