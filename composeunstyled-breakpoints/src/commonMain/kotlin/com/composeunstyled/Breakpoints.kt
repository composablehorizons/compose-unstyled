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
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.jvm.JvmInline

@Immutable
@JvmInline
value class WidthBreakpoint(val name: String) {
  override fun toString(): String {
    return name
  }
}

@Immutable
@JvmInline
value class HeightBreakpoint(val name: String) {
  override fun toString(): String {
    return name
  }
}

@Immutable
class WindowWidthBreakpoints internal constructor(
  private val breakpoints: BreakpointScale<WidthBreakpoint>,
) {
  internal fun resolve(width: Dp): ResolvedWidthBreakpoint {
    return ResolvedWidthBreakpoint(
      value = breakpoints.breakpointFor(width),
      breakpoints = breakpoints,
    )
  }
}

fun WindowWidthBreakpoints(
  content: WindowWidthBreakpointsBuilder.() -> Unit,
): WindowWidthBreakpoints {
  return WindowWidthBreakpoints(
    WindowWidthBreakpointsBuilder()
      .apply(content)
      .build(),
  )
}

@Immutable
class WindowHeightBreakpoints internal constructor(
  private val breakpoints: BreakpointScale<HeightBreakpoint>,
) {
  internal fun resolve(height: Dp): ResolvedHeightBreakpoint {
    return ResolvedHeightBreakpoint(
      value = breakpoints.breakpointFor(height),
      breakpoints = breakpoints,
    )
  }
}

fun WindowHeightBreakpoints(
  content: WindowHeightBreakpointsBuilder.() -> Unit,
): WindowHeightBreakpoints {
  return WindowHeightBreakpoints(
    WindowHeightBreakpointsBuilder()
      .apply(content)
      .build(),
  )
}

class WindowWidthBreakpointsBuilder {
  private val breakpoints = mutableListOf<BreakpointThreshold<WidthBreakpoint>>()

  infix fun WidthBreakpoint.startsAt(minWidth: Dp) {
    breakpoints += BreakpointThreshold(this, minWidth)
  }

  internal fun build(): BreakpointScale<WidthBreakpoint> {
    return BreakpointScale.from(breakpoints)
  }
}

class WindowHeightBreakpointsBuilder {
  private val breakpoints = mutableListOf<BreakpointThreshold<HeightBreakpoint>>()

  infix fun HeightBreakpoint.startsAt(minHeight: Dp) {
    breakpoints += BreakpointThreshold(this, minHeight)
  }

  internal fun build(): BreakpointScale<HeightBreakpoint> {
    return BreakpointScale.from(breakpoints)
  }
}

@Immutable
class ResolvedWidthBreakpoint internal constructor(
  val value: WidthBreakpoint,
  private val breakpoints: BreakpointScale<WidthBreakpoint>,
) {
  val name: String
    get() = value.name

  infix fun isAt(breakpoint: WidthBreakpoint): Boolean {
    return value == breakpoint
  }

  infix fun isAtLeast(breakpoint: WidthBreakpoint): Boolean {
    return breakpoints.isAtLeast(value, breakpoint)
  }

  infix fun isBelow(breakpoint: WidthBreakpoint): Boolean {
    return (this isAtLeast breakpoint).not()
  }

  override fun toString(): String {
    return name
  }
}

@Immutable
class ResolvedHeightBreakpoint internal constructor(
  val value: HeightBreakpoint,
  private val breakpoints: BreakpointScale<HeightBreakpoint>,
) {
  val name: String
    get() = value.name

  infix fun isAt(breakpoint: HeightBreakpoint): Boolean {
    return value == breakpoint
  }

  infix fun isAtLeast(breakpoint: HeightBreakpoint): Boolean {
    return breakpoints.isAtLeast(value, breakpoint)
  }

  infix fun isBelow(breakpoint: HeightBreakpoint): Boolean {
    return (this isAtLeast breakpoint).not()
  }

  override fun toString(): String {
    return name
  }
}

private val LocalWindowWidthBreakpoints = staticCompositionLocalOf<WindowWidthBreakpoints> {
  error(
    "No window width breakpoints provided. " +
      "Wrap the content with ProvideWindowWidthBreakpoints(...).",
  )
}

private val LocalWindowHeightBreakpoints = staticCompositionLocalOf<WindowHeightBreakpoints> {
  error(
    "No window height breakpoints provided. " +
      "Wrap the content with ProvideWindowHeightBreakpoints(...).",
  )
}

@Composable
fun ProvideWindowBreakpoints(
  width: WindowWidthBreakpoints,
  height: WindowHeightBreakpoints,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    LocalWindowWidthBreakpoints provides width,
    LocalWindowHeightBreakpoints provides height,
    content = content,
  )
}

@Composable
fun ProvideWindowWidthBreakpoints(
  breakpoints: WindowWidthBreakpoints,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    LocalWindowWidthBreakpoints provides breakpoints,
    content = content,
  )
}

@Composable
fun ProvideWindowHeightBreakpoints(
  breakpoints: WindowHeightBreakpoints,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    LocalWindowHeightBreakpoints provides breakpoints,
    content = content,
  )
}

@Composable
fun currentWindowWidthBreakpoint(): ResolvedWidthBreakpoint {
  val breakpoints = LocalWindowWidthBreakpoints.current
  val size = LocalWindowInfo.current.containerDpSize
  val resolved = breakpoints.resolve(size.width)
  return remember(breakpoints, resolved.value) {
    resolved
  }
}

@Composable
fun currentWindowHeightBreakpoint(): ResolvedHeightBreakpoint {
  val breakpoints = LocalWindowHeightBreakpoints.current
  val size = LocalWindowInfo.current.containerDpSize
  val resolved = breakpoints.resolve(size.height)
  return remember(breakpoints, resolved.value) {
    resolved
  }
}

@Immutable
internal data class BreakpointThreshold<T>(
  val breakpoint: T,
  val minSize: Dp,
)

@Immutable
internal class BreakpointScale<T> private constructor(
  private val breakpoints: List<BreakpointThreshold<T>>,
) {
  fun breakpointFor(size: Dp): T {
    return breakpoints.last { size >= it.minSize }.breakpoint
  }

  fun isAtLeast(current: T, breakpoint: T): Boolean {
    val currentRank = rankOf(current)
    val breakpointRank = rankOf(breakpoint)
    if (currentRank == null || breakpointRank == null) return current == breakpoint
    return currentRank >= breakpointRank
  }

  private fun rankOf(breakpoint: T): Int? {
    val index = breakpoints.indexOfFirst { it.breakpoint == breakpoint }
    return if (index >= 0) index else null
  }

  companion object {
    fun <T> from(breakpoints: List<BreakpointThreshold<T>>): BreakpointScale<T> {
      require(breakpoints.isNotEmpty()) {
        "Window breakpoints must define at least one breakpoint."
      }
      val negativeBreakpoints = breakpoints.filter { it.minSize < 0.dp }
      require(negativeBreakpoints.isEmpty()) {
        "Window breakpoints must not define negative sizes. " +
          "Invalid breakpoints: ${negativeBreakpoints.joinToString { it.describe() }}."
      }
      require(breakpoints.any { it.minSize == 0.dp }) {
        "Window breakpoints must define a breakpoint with startsAt(0.dp). " +
          "Defined breakpoints: ${breakpoints.joinToString { it.describe() }}."
      }
      require(breakpoints.distinctBy { it.breakpoint }.size == breakpoints.size) {
        "Window breakpoints must not define the same breakpoint more than once."
      }
      val breakpointSizeConflicts = breakpoints
        .groupBy { it.minSize }
        .filterValues { it.size > 1 }
      require(breakpointSizeConflicts.isEmpty()) {
        "Window breakpoints must not define more than one breakpoint at the same size. " +
          "Conflicts: ${breakpointSizeConflicts.entries.joinToString { it.describe() }}."
      }

      return BreakpointScale(
        breakpoints = breakpoints.sortedBy { it.minSize },
      )
    }

    private fun <T> BreakpointThreshold<T>.describe(): String {
      return "$breakpoint @ ${minSize.describe()}"
    }

    private fun <T> Map.Entry<Dp, List<BreakpointThreshold<T>>>.describe(): String {
      return "${key.describe()}: ${value.joinToString { it.breakpoint.toString() }}"
    }

    private fun Dp.describe(): String {
      val wholeValue = value.toInt()
      return if (value == wholeValue.toFloat()) {
        "$wholeValue.dp"
      } else {
        "$value.dp"
      }
    }
  }
}
