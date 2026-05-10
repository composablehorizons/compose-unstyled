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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.composeunstyled.DialogPanel
import com.composeunstyled.Scrim
import com.composeunstyled.UnstyledDialog

private val DialogMinWidth = 280.dp
private val DialogMaxWidth = 560.dp
private val DialogPadding = PaddingValues(24.dp)
private val DialogIconPadding = PaddingValues(bottom = 16.dp)
private val DialogTitlePadding = PaddingValues(bottom = 16.dp)
private val DialogTextPadding = PaddingValues(bottom = 24.dp)
private const val DialogEnterDurationMillis = 150
private const val DialogExitDurationMillis = 75
private const val DialogFadeInDurationMillis = 50

@Composable
fun AlertDialog(
  onDismissRequest: () -> Unit,
  confirmButton: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  visible: Boolean = true,
  dismissButton: @Composable (() -> Unit)? = null,
  icon: @Composable (() -> Unit)? = null,
  title: @Composable (() -> Unit)? = null,
  text: @Composable (() -> Unit)? = null,
  shape: Shape = AlertDialogDefaults.shape,
  containerColor: Color = AlertDialogDefaults.containerColor,
  iconContentColor: Color = AlertDialogDefaults.iconContentColor,
  titleContentColor: Color = AlertDialogDefaults.titleContentColor,
  textContentColor: Color = AlertDialogDefaults.textContentColor,
  tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
  properties: DialogProperties = DialogProperties(),
) {
  UnstyledDialog(
    visible = visible,
    onDismissRequest = onDismissRequest,
    properties = com.composeunstyled.DialogProperties(
      dismissOnBackPress = properties.dismissOnBackPress,
      dismissOnClickOutside = properties.dismissOnClickOutside,
    ),
    overlay = {
      Scrim(
        enter = fadeIn(animationSpec = tween(DialogEnterDurationMillis)),
        exit = fadeOut(animationSpec = tween(DialogExitDurationMillis)),
      )
    },
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center,
    ) {
      DialogPanel(
        modifier = modifier
          .sizeIn(minWidth = DialogMinWidth, maxWidth = DialogMaxWidth)
          .clip(shape)
          .background(containerColor, shape),
        paneTitle = "Dialog",
        enter = fadeIn(animationSpec = tween(DialogFadeInDurationMillis)) +
          scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(DialogEnterDurationMillis),
          ),
        exit = fadeOut(animationSpec = tween(DialogExitDurationMillis)) +
          scaleOut(
            targetScale = 0.95f,
            animationSpec = tween(DialogExitDurationMillis),
          ),
        contentPadding = DialogPadding,
      ) {
        Column {
          icon?.let {
            CompositionLocalProvider(LocalContentColor provides iconContentColor) {
              Box(Modifier.padding(DialogIconPadding).align(Alignment.CenterHorizontally)) {
                it()
              }
            }
          }
          title?.let {
            CompositionLocalProvider(LocalContentColor provides titleContentColor) {
              ProvideTextStyle(MaterialTheme.typography.headlineSmall) {
                Box(
                  Modifier
                    .padding(DialogTitlePadding)
                    .align(
                      if (icon == null) {
                        Alignment.Start
                      } else {
                        Alignment.CenterHorizontally
                      },
                    ),
                ) {
                  it()
                }
              }
            }
          }
          text?.let {
            CompositionLocalProvider(LocalContentColor provides textContentColor) {
              ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                Box(
                  Modifier
                    .weight(weight = 1f, fill = false)
                    .padding(DialogTextPadding)
                    .align(Alignment.Start),
                ) {
                  it()
                }
              }
            }
          }
          Box(modifier = Modifier.align(Alignment.End)) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
              ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                Row(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  dismissButton?.invoke()
                  confirmButton()
                }
              }
            }
          }
        }
      }
    }
  }
}
