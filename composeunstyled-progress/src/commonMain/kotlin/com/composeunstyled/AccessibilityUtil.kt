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
package com.composeunstyled

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset

internal val HorizontalSemanticsBoundsPadding: Dp = 10.dp
internal val VerticalSemanticsBoundsPadding: Dp = 10.dp

internal val IncreaseHorizontalSemanticsBounds: Modifier =
  Modifier.layout { measurable, constraints ->
    val paddingPx = HorizontalSemanticsBoundsPadding.roundToPx()
    // We need to add horizontal padding to the semantics bounds in order to meet
    // screenreader green box minimum size, but we also want to
    // preserve a visual appearance and layout size below that minimum
    // in order to maintain backwards compatibility. This custom
    // layout effectively implements "negative padding".
    val newConstraint = constraints.offset(paddingPx * 2, 0)
    val placeable = measurable.measure(newConstraint)

    // But when actually placing the placeable, create the layout without additional
    // space. Place the placeable where it would've been without any extra padding.
    val height = placeable.height
    val width = placeable.width - paddingPx * 2
    layout(width, height) { placeable.place(-paddingPx, 0) }
  }
    .semantics(mergeDescendants = true) {}
    .padding(horizontal = HorizontalSemanticsBoundsPadding)

internal val IncreaseVerticalSemanticsBounds: Modifier =
  Modifier.layout { measurable, constraints ->
    val paddingPx = VerticalSemanticsBoundsPadding.roundToPx()
    // We need to add vertical padding to the semantics bounds in order to meet
    // screenreader green box minimum size, but we also want to
    // preserve a visual appearance and layout size below that minimum
    // in order to maintain backwards compatibility. This custom
    // layout effectively implements "negative padding".
    val newConstraint = constraints.offset(0, paddingPx * 2)
    val placeable = measurable.measure(newConstraint)

    // But when actually placing the placeable, create the layout without additional
    // space. Place the placeable where it would've been without any extra padding.
    val height = placeable.height - paddingPx * 2
    val width = placeable.width
    layout(width, height) { placeable.place(0, -paddingPx) }
  }
    .semantics(mergeDescendants = true) {}
    .padding(vertical = VerticalSemanticsBoundsPadding)
