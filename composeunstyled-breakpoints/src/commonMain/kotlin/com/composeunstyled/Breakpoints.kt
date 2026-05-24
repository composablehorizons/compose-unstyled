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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp

@Immutable
class ScreenBreakpoints private constructor(
  private val breakpoints: List<ScreenBreakpoint>,
) {
  constructor() : this(emptyList())

  constructor(content: ScreenBreakpointsBuilder.() -> Unit) : this(
    ScreenBreakpointsBuilder()
      .apply(content)
      .build(),
  )

  fun breakpointFor(size: Dp): ResolvedBreakpoint {
    val breakpoint = breakpoints.lastOrNull { size >= it.minSize }?.breakpoint ?: Breakpoint.Base
    return ResolvedBreakpoint(breakpoint, this)
  }

  internal fun isAtLeast(current: Breakpoint, breakpoint: Breakpoint): Boolean {
    val currentRank = rankOf(current)
    val breakpointRank = rankOf(breakpoint)
    if (currentRank == null || breakpointRank == null) return current == breakpoint
    return currentRank >= breakpointRank
  }

  private fun rankOf(breakpoint: Breakpoint): Int? {
    if (breakpoint == Breakpoint.Base) return -1
    val index = breakpoints.indexOfFirst { it.breakpoint == breakpoint }
    return if (index >= 0) index else null
  }
}

@Immutable
internal data class ScreenBreakpoint(
  val breakpoint: Breakpoint,
  val minSize: Dp,
)

class ScreenBreakpointsBuilder {
  private val breakpoints = mutableListOf<ScreenBreakpoint>()

  infix fun Breakpoint.at(minSize: Dp) {
    breakpoints += ScreenBreakpoint(this, minSize)
  }

  internal fun build(): List<ScreenBreakpoint> {
    return breakpoints.sortedBy { it.minSize }
  }
}

@Immutable
class ResolvedBreakpoint internal constructor(
  val breakpoint: Breakpoint,
  private val screenBreakpoints: ScreenBreakpoints,
) {
  val name: String
    get() = breakpoint.name

  infix fun isAtLeast(breakpoint: Breakpoint): Boolean {
    return screenBreakpoints.isAtLeast(this.breakpoint, breakpoint)
  }

  override fun toString(): String {
    return name
  }
}

val LocalWidthBreakpoints = staticCompositionLocalOf { ScreenBreakpoints() }
val LocalHeightBreakpoints = staticCompositionLocalOf { ScreenBreakpoints() }

@Composable
fun ProvideBreakpoints(
  widthBreakpoints: ScreenBreakpoints = ScreenBreakpoints(),
  heightBreakpoints: ScreenBreakpoints = ScreenBreakpoints(),
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    LocalWidthBreakpoints provides widthBreakpoints,
    LocalHeightBreakpoints provides heightBreakpoints,
  ) {
    content()
  }
}

@Composable
fun currentWidthBreakpoint(): ResolvedBreakpoint {
  val size = currentWindowContainerSize()
  val breakpoints = LocalWidthBreakpoints.current
  return breakpoints.breakpointFor(size.width)
}

@Composable
fun currentHeightBreakpoint(): ResolvedBreakpoint {
  val size = currentWindowContainerSize()
  val breakpoints = LocalHeightBreakpoints.current
  return breakpoints.breakpointFor(size.height)
}

@Immutable
data class Breakpoint(val name: String) {
  companion object {
    val Base = Breakpoint("base")
  }
}
