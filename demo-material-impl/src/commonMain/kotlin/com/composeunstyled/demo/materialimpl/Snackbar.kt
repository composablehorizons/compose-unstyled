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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun Snackbar(
  modifier: Modifier = Modifier,
  action: @Composable (() -> Unit)? = null,
  dismissAction: @Composable (() -> Unit)? = null,
  actionOnNewLine: Boolean = false,
  shape: Shape = SnackbarDefaults.shape,
  containerColor: Color = SnackbarDefaults.color,
  contentColor: Color = SnackbarDefaults.contentColor,
  actionContentColor: Color = SnackbarDefaults.actionContentColor,
  dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
  content: @Composable () -> Unit,
) {
  Surface(
    modifier = modifier,
    shape = shape,
    color = containerColor,
    contentColor = contentColor,
  ) {
    if (actionOnNewLine) {
      Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        content()
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
          action?.invoke()
          dismissAction?.invoke()
        }
      }
    } else {
      Row(
        Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Box(Modifier.weight(1f)) { content() }
        action?.invoke()
        dismissAction?.invoke()
      }
    }
  }
}

@Composable
fun Snackbar(
  snackbarData: androidx.compose.material3.SnackbarData,
  modifier: Modifier = Modifier,
  actionOnNewLine: Boolean = false,
  shape: Shape = SnackbarDefaults.shape,
  containerColor: Color = SnackbarDefaults.color,
  contentColor: Color = SnackbarDefaults.contentColor,
  actionColor: Color = SnackbarDefaults.actionColor,
  actionContentColor: Color = SnackbarDefaults.actionContentColor,
  dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
) {
  Snackbar(
    modifier = modifier,
    action = snackbarData.visuals.actionLabel?.let { label ->
      {
        TextButton(onClick = { snackbarData.performAction() }) {
          Text(label, color = actionColor)
        }
      }
    },
    dismissAction = if (snackbarData.visuals.withDismissAction) {
      {
        TextButton(onClick = { snackbarData.dismiss() }) {
          Text("Dismiss", color = dismissActionContentColor)
        }
      }
    } else {
      null
    },
    actionOnNewLine = actionOnNewLine,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    actionContentColor = actionContentColor,
    dismissActionContentColor = dismissActionContentColor,
  ) {
    Text(snackbarData.visuals.message)
  }
}

@Composable
fun SnackbarHost(
  hostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  snackbar: @Composable (androidx.compose.material3.SnackbarData) -> Unit = { Snackbar(it) },
) {
  Box(modifier) {
    hostState.currentSnackbarData?.let { snackbar(it) }
  }
}
