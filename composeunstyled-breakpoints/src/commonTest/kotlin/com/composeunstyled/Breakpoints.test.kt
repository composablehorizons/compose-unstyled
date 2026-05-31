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

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class BreakpointsTest {
  private val compact = WidthBreakpoint("compact")
  private val medium = WidthBreakpoint("medium")
  private val expanded = WidthBreakpoint("expanded")
  private val large = WidthBreakpoint("large")

  @Test
  fun resolve_returns_the_first_breakpoint_before_the_next_threshold() {
    val breakpoints = WindowWidthBreakpoints {
      compact startsAt 0.dp
      medium startsAt 600.dp
    }

    assertThat(breakpoints.resolve(320.dp).value).isEqualTo(compact)
  }

  @Test
  fun resolve_returns_the_matching_breakpoint_at_its_threshold() {
    val breakpoints = WindowWidthBreakpoints {
      compact startsAt 0.dp
      medium startsAt 600.dp
    }

    assertThat(breakpoints.resolve(600.dp).value).isEqualTo(medium)
  }

  @Test
  fun resolve_returns_the_highest_reached_breakpoint() {
    val breakpoints = WindowWidthBreakpoints {
      compact startsAt 0.dp
      medium startsAt 600.dp
      expanded startsAt 840.dp
    }

    assertThat(breakpoints.resolve(700.dp).value).isEqualTo(medium)
  }

  @Test
  fun resolve_sticks_to_the_last_breakpoint_above_the_highest_threshold() {
    val breakpoints = WindowWidthBreakpoints {
      compact startsAt 0.dp
      medium startsAt 600.dp
      expanded startsAt 840.dp
      large startsAt 1200.dp
    }

    assertThat(breakpoints.resolve(1600.dp).value).isEqualTo(large)
  }

  @Test
  fun builder_sorts_breakpoints_by_min_size() {
    val breakpoints = WindowWidthBreakpoints {
      expanded startsAt 840.dp
      compact startsAt 0.dp
      medium startsAt 600.dp
    }

    assertThat(breakpoints.resolve(700.dp).value).isEqualTo(medium)
  }

  @Test
  fun current_breakpoint_compares_against_its_breakpoint_set() {
    val breakpoints = WindowWidthBreakpoints {
      compact startsAt 0.dp
      medium startsAt 600.dp
      expanded startsAt 840.dp
    }

    val resolved = breakpoints.resolve(700.dp)

    assertThat(resolved isAtLeast compact).isTrue()
    assertThat(resolved isAtLeast medium).isTrue()
    assertThat(resolved isAtLeast expanded).isFalse()
  }

  @Test
  fun current_breakpoint_checks_if_it_is_at_a_breakpoint() {
    val breakpoints = WindowWidthBreakpoints {
      compact startsAt 0.dp
      medium startsAt 600.dp
      expanded startsAt 840.dp
    }

    val resolved = breakpoints.resolve(700.dp)

    assertThat(resolved isAt compact).isFalse()
    assertThat(resolved isAt medium).isTrue()
    assertThat(resolved isAt expanded).isFalse()
  }

  @Test
  fun current_breakpoint_checks_if_it_is_below_a_breakpoint() {
    val breakpoints = WindowWidthBreakpoints {
      compact startsAt 0.dp
      medium startsAt 600.dp
      expanded startsAt 840.dp
    }

    val resolved = breakpoints.resolve(700.dp)

    assertThat(resolved isBelow compact).isFalse()
    assertThat(resolved isBelow medium).isFalse()
    assertThat(resolved isBelow expanded).isTrue()
  }

  @Test
  fun breakpoint_names_are_value_based() {
    val first = WidthBreakpoint("expanded")
    val second = WidthBreakpoint("expanded")

    assertThat(first).isEqualTo(second)
  }

  @Test
  fun height_breakpoints_resolve_height_values() {
    val short = HeightBreakpoint("short")
    val tall = HeightBreakpoint("tall")
    val taller = HeightBreakpoint("taller")
    val breakpoints = WindowHeightBreakpoints {
      short startsAt 0.dp
      tall startsAt 720.dp
      taller startsAt 900.dp
    }

    val resolved = breakpoints.resolve(800.dp)

    assertThat(resolved isAt tall).isTrue()
    assertThat(resolved isAtLeast short).isTrue()
    assertThat(resolved isBelow short).isFalse()
    assertThat(resolved isBelow tall).isFalse()
    assertThat(resolved isBelow taller).isTrue()
  }

  @Test
  fun provides_window_width_breakpoints_to_content() = runComposeUiTest {
    val widthBreakpoints = WindowWidthBreakpoints {
      compact startsAt 0.dp
      medium startsAt 600.dp
    }
    var resolved: WidthBreakpoint? = null

    setContent {
      ProvideWindowWidthBreakpoints(widthBreakpoints) {
        resolved = widthBreakpoints.resolve(700.dp).value
      }
    }

    assertThat(resolved).isEqualTo(medium)
  }

  @Test
  fun provides_window_height_breakpoints_to_content() = runComposeUiTest {
    val short = HeightBreakpoint("short")
    val tall = HeightBreakpoint("tall")
    val breakpoints = WindowHeightBreakpoints {
      short startsAt 0.dp
      tall startsAt 720.dp
    }
    var resolved: HeightBreakpoint? = null

    setContent {
      ProvideWindowHeightBreakpoints(breakpoints) {
        resolved = breakpoints.resolve(800.dp).value
      }
    }

    assertThat(resolved).isEqualTo(tall)
  }

  @Test
  fun provides_window_width_and_height_breakpoints_to_content() = runComposeUiTest {
    val short = HeightBreakpoint("short")
    val tall = HeightBreakpoint("tall")
    val widthBreakpoints = WindowWidthBreakpoints {
      compact startsAt 0.dp
      medium startsAt 600.dp
    }
    val heightBreakpoints = WindowHeightBreakpoints {
      short startsAt 0.dp
      tall startsAt 720.dp
    }
    var resolvedWidth: WidthBreakpoint? = null
    var resolvedHeight: HeightBreakpoint? = null

    setContent {
      ProvideWindowBreakpoints(
        width = widthBreakpoints,
        height = heightBreakpoints,
      ) {
        resolvedWidth = widthBreakpoints.resolve(700.dp).value
        resolvedHeight = heightBreakpoints.resolve(800.dp).value
      }
    }

    assertThat(resolvedWidth).isEqualTo(medium)
    assertThat(resolvedHeight).isEqualTo(tall)
  }

  @Test
  fun builder_rejects_empty_breakpoint_scales() {
    assertFailure {
      WindowWidthBreakpoints {}
    }.hasMessage("Window breakpoints must define at least one breakpoint.")
  }

  @Test
  fun builder_requires_a_breakpoint_at_zero() {
    assertFailure {
      WindowWidthBreakpoints {
        medium startsAt 600.dp
      }
    }.hasMessage(
      "Window breakpoints must define a breakpoint with startsAt(0.dp). " +
        "Defined breakpoints: medium @ 600.dp.",
    )
  }

  @Test
  fun builder_rejects_duplicate_breakpoints() {
    assertFailure {
      WindowWidthBreakpoints {
        compact startsAt 0.dp
        compact startsAt 600.dp
      }
    }.hasMessage("Window breakpoints must not define the same breakpoint more than once.")
  }

  @Test
  fun builder_rejects_duplicate_min_sizes() {
    assertFailure {
      WindowWidthBreakpoints {
        compact startsAt 0.dp
        medium startsAt 0.dp
      }
    }.hasMessage(
      "Window breakpoints must not define more than one breakpoint at the same size. " +
        "Conflicts: 0.dp: compact, medium.",
    )
  }

  @Test
  fun builder_rejects_negative_min_sizes() {
    assertFailure {
      WindowWidthBreakpoints {
        compact startsAt 0.dp
        medium startsAt (-1).dp
      }
    }.hasMessage(
      "Window breakpoints must not define negative sizes. " +
        "Invalid breakpoints: medium @ -1.dp.",
    )
  }

  @Test
  fun current_breakpoint_does_not_compare_as_at_least_unknown_breakpoints() {
    val breakpoints = WindowWidthBreakpoints {
      compact startsAt 0.dp
      large startsAt 1200.dp
    }
    val unknown = WidthBreakpoint("unknown")

    val resolved = breakpoints.resolve(1600.dp)

    assertThat(resolved isAtLeast unknown).isFalse()
  }
}
