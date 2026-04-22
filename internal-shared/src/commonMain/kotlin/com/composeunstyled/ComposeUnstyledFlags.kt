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
@file:Suppress("ktlint:standard:max-line-length")

package com.composeunstyled

object ComposeUnstyledFlags {
  /**
   * Changes the order in which the text colors are resolved in the [Text] and [TextField] components.
   *
   * When setting to true, the order becomes:
   *
   * [androidx.compose.ui.graphics.Color] -> [androidx.compose.ui.text.TextStyle] -> [LocalContentColor]
   *
   * Will be enabled by default in 2.0
   */
  var strictTextColorResolutionOrder = false

  /**
   * Controls the default [androidx.compose.foundation.Indication] in [com.composeunstyled.theme.Theme]s created by the [com.composeunstyled.theme.buildTheme] function.
   *
   * When set to true, the default indication becomes [com.composeunstyled.theme.NoIndication]
   *
   * When false, the default indication is the current value of [androidx.compose.foundation.LocalIndication].
   *
   *
   * Will be enabled by default in 2.0
   */
  var noDefaultThemeIndication = false
}
