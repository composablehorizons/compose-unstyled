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

import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class BreakpointsTest {
  private val sm = Breakpoint("sm")
  private val md = Breakpoint("md")
  private val lg = Breakpoint("lg")
  private val xl = Breakpoint("xl")

  @Test
  fun breakpointfor_returns_base_before_the_first_breakpoint() {
    val breakpoints = ScreenBreakpoints {
      sm at 640.dp
      md at 768.dp
    }

    assertThat(breakpoints.breakpointFor(320.dp).breakpoint).isEqualTo(Breakpoint.Base)
  }

  @Test
  fun breakpointfor_returns_the_matching_breakpoint_at_its_threshold() {
    val breakpoints = ScreenBreakpoints {
      sm at 640.dp
      md at 768.dp
    }

    assertThat(breakpoints.breakpointFor(640.dp).breakpoint).isEqualTo(sm)
  }

  @Test
  fun breakpointfor_returns_the_highest_reached_breakpoint() {
    val breakpoints = ScreenBreakpoints {
      sm at 640.dp
      md at 768.dp
      lg at 1024.dp
    }

    assertThat(breakpoints.breakpointFor(900.dp).breakpoint).isEqualTo(md)
  }

  @Test
  fun breakpointfor_sticks_to_the_last_breakpoint_above_the_highest_threshold() {
    val breakpoints = ScreenBreakpoints {
      sm at 640.dp
      md at 768.dp
      lg at 1024.dp
      xl at 1280.dp
    }

    assertThat(breakpoints.breakpointFor(1600.dp).breakpoint).isEqualTo(xl)
  }

  @Test
  fun builder_sorts_breakpoints_by_min_size() {
    val breakpoints = ScreenBreakpoints {
      lg at 1024.dp
      sm at 640.dp
      md at 768.dp
    }

    assertThat(breakpoints.breakpointFor(900.dp).breakpoint).isEqualTo(md)
  }

  @Test
  fun resolved_breakpoint_compares_against_its_breakpoint_set() {
    val breakpoints = ScreenBreakpoints {
      sm at 640.dp
      md at 768.dp
      lg at 1024.dp
    }

    val resolved = breakpoints.breakpointFor(900.dp)

    assertThat(resolved isAtLeast sm).isTrue()
    assertThat(resolved isAtLeast md).isTrue()
    assertThat(resolved isAtLeast lg).isFalse()
  }

  @Test
  fun base_is_at_least_base_but_not_configured_breakpoints() {
    val breakpoints = ScreenBreakpoints {
      sm at 640.dp
    }

    val resolved = breakpoints.breakpointFor(320.dp)

    assertThat(resolved isAtLeast Breakpoint.Base).isTrue()
    assertThat(resolved isAtLeast sm).isFalse()
  }

  @Test
  fun resolved_breakpoint_does_not_compare_against_unknown_breakpoints_from_another_scale() {
    val widthBreakpoints = ScreenBreakpoints {
      sm at 640.dp
      xl at 1280.dp
    }
    val tall = Breakpoint("tall")

    val resolved = widthBreakpoints.breakpointFor(1600.dp)

    assertThat(resolved isAtLeast tall).isFalse()
  }
}
