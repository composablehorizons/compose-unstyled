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

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class ScrollBarsTest {

  @Test
  fun always_visible_keeps_the_thumb_visible() = runComposeUiTest {
    setContent {
      val scrollState = rememberScrollState()
      val scrollAreaState = rememberScrollAreaState(scrollState)
      ScrollArea(state = scrollAreaState) {
        Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState)) {
          repeat(100) { index ->
            BasicText("Item $index")
          }
        }
        UnstyledVerticalScrollbar(
          scrollAreaState = scrollAreaState,
          modifier = Modifier.testTag("scrollbar"),
        ) {
          Thumb(
            modifier = Modifier.testTag("thumb").width(8.dp),
            thumbVisibility = ThumbVisibility.AlwaysVisible,
          )
        }
      }
    }

    onNodeWithTag("thumb").assertIsDisplayed()
    advanceTimeBy(60.seconds)
    onNodeWithTag("thumb").assertIsDisplayed()
  }

  @Test
  fun hide_while_idle_starts_with_thumb_out_of_composition() = runComposeUiTest {
    setContent {
      val scrollState = rememberScrollState()
      val scrollAreaState = rememberScrollAreaState(scrollState)
      ScrollArea(state = scrollAreaState) {
        Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState)) {
          repeat(100) { index ->
            BasicText("Item $index")
          }
        }
        UnstyledVerticalScrollbar(
          scrollAreaState = scrollAreaState,
          modifier = Modifier.testTag("scrollbar"),
        ) {
          Thumb(
            modifier = Modifier.testTag("thumb").width(8.dp),
            thumbVisibility = ThumbVisibility.HideWhileIdle(
              enter = EnterTransition.None,
              exit = ExitTransition.None,
              hideDelay = 5.seconds,
            ),
          )
        }
      }
    }

    onNodeWithTag("thumb").assertDoesNotExist()
  }

  @Test
  fun hide_while_idle_shows_thumb_when_list_is_scrolled() = runComposeUiTest {
    setContent {
      val scrollState = rememberScrollState()
      val scrollAreaState = rememberScrollAreaState(scrollState)
      ScrollArea(state = scrollAreaState) {
        Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState).testTag("list")) {
          repeat(100) { index ->
            BasicText("Item $index")
          }
        }
        UnstyledVerticalScrollbar(
          scrollAreaState = scrollAreaState,
          modifier = Modifier.testTag("scrollbar"),
        ) {
          Thumb(
            modifier = Modifier.testTag("thumb").width(8.dp),
            thumbVisibility = ThumbVisibility.HideWhileIdle(
              enter = EnterTransition.None,
              exit = ExitTransition.None,
              hideDelay = 5.seconds,
            ),
          )
        }
      }
    }
    onNodeWithTag("thumb").assertDoesNotExist()
    onNodeWithTag("list").performTouchInput {
      swipeUp()
    }
    onNodeWithTag("thumb").assertIsDisplayed()
  }

  @Test
  fun hide_while_idle_hides_thumb_after_given_time_after_done_scrolling() = runComposeUiTest {
    // TODO this checks for drags, not scrolls
    setContent {
      val scrollState = rememberScrollState()
      val scrollAreaState = rememberScrollAreaState(scrollState)
      ScrollArea(state = scrollAreaState) {
        Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState).testTag("list")) {
          repeat(100) { index ->
            BasicText("Item $index")
          }
        }
        UnstyledVerticalScrollbar(
          scrollAreaState = scrollAreaState,
          modifier = Modifier.testTag("scrollbar")
            .align(Alignment.CenterEnd),
        ) {
          Thumb(
            modifier = Modifier.testTag("thumb").width(8.dp),
            thumbVisibility = ThumbVisibility.HideWhileIdle(
              enter = EnterTransition.None,
              exit = ExitTransition.None,
              hideDelay = 5.seconds,
            ),
          )
        }
      }
    }
    // time = 0.second
    onNodeWithTag("thumb").assertDoesNotExist()

    onNodeWithTag("list").performTouchInput {
      swipeUp(durationMillis = 1_000)
    }
    // time = 1.second
    onNodeWithTag("thumb").assertIsDisplayed()

    // time = 4.second
    advanceTimeBy(3.seconds)
    onNodeWithTag("thumb").assertIsDisplayed()

    // time = 6.second
    advanceTimeBy(2.seconds)
    onNodeWithTag("thumb").assertDoesNotExist()
  }

  @Test
  fun hide_while_idle_does_not_hide_thumb_while_dragging() = runComposeUiTest {
    setContent {
      val scrollState = rememberScrollState()
      val scrollAreaState = rememberScrollAreaState(scrollState)
      ScrollArea(state = scrollAreaState) {
        Column(
          modifier = Modifier
            .height(200.dp)
            .verticalScroll(scrollState)
            .testTag("list"),
        ) {
          repeat(100) { BasicText("Item $it") }
        }
        UnstyledVerticalScrollbar(
          scrollAreaState = scrollAreaState,
          modifier = Modifier
            .testTag("scrollbar")
            .align(Alignment.CenterEnd),
        ) {
          Thumb(
            modifier = Modifier.testTag("thumb").width(8.dp),
            thumbVisibility = ThumbVisibility.HideWhileIdle(
              enter = EnterTransition.None,
              exit = ExitTransition.None,
              hideDelay = 2.seconds,
            ),
          )
        }
      }
    }

    // Initially hidden
    onNodeWithTag("thumb").assertDoesNotExist()

    // Start drag (finger goes down)
    onNodeWithTag("list").performTouchInput {
      down(center)
      moveBy(Offset(0f, -50f))
    }

    // Let Compose process scroll + visibility
    waitForIdle()

    // assert while drag is still active
    onNodeWithTag("thumb").assertIsDisplayed()

    // Advance time beyond hideDelay while still dragging
    advanceTimeBy(3.seconds)
    waitForIdle()

    // must still be visible while drag is active
    onNodeWithTag("thumb").assertIsDisplayed()

    // End drag
    onNodeWithTag("list").performTouchInput {
      up()
    }

    // Immediately after release → still visible
    waitForIdle()
    onNodeWithTag("thumb").assertIsDisplayed()

    // After hideDelay → hidden
    advanceTimeBy(2.seconds)
    waitForIdle()
    onNodeWithTag("thumb").assertDoesNotExist()
  }

  @Test
  fun hide_while_idle_does_not_hide_thumb_while_dragging_the_thumb() = runComposeUiTest {
    setContent {
      val scrollState = rememberScrollState()
      val scrollAreaState = rememberScrollAreaState(scrollState)
      ScrollArea(state = scrollAreaState) {
        Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState).testTag("list")) {
          repeat(100) { index ->
            BasicText("Item $index")
          }
        }
        UnstyledVerticalScrollbar(
          scrollAreaState = scrollAreaState,
          modifier = Modifier.testTag("scrollbar"),
        ) {
          Thumb(
            modifier = Modifier.testTag("thumb").width(8.dp),
            thumbVisibility = ThumbVisibility.HideWhileIdle(
              enter = EnterTransition.None,
              exit = ExitTransition.None,
              hideDelay = 5.seconds,
            ),
          )
        }
      }
    }

    onNodeWithTag("thumb").assertDoesNotExist()

    onNodeWithTag("list").performTouchInput {
      swipeUp(durationMillis = 1_000)
    }
    // Drag the thumb
    onNodeWithTag("thumb").performTouchInput {
      down(Offset(5f, 5f)) // assuming thumb is small
      moveTo(Offset(5f, 50f))
      // Keep dragging, don't up yet
    }
    onNodeWithTag("thumb").assertIsDisplayed()

    // Advance time beyond hideDelay while dragging
    advanceTimeBy(6.seconds)
    onNodeWithTag("thumb").assertIsDisplayed() // Should still be visible

    // Stop dragging
    onNodeWithTag("thumb").performTouchInput { up() }

    // Advance time beyond hideDelay
    advanceTimeBy(6.seconds)
    waitForIdle()
    onNodeWithTag("thumb").assertDoesNotExist()
  }

  @Test
  fun hide_while_idle_keeps_thumb_visible_when_pointer_leaves_track_during_thumb_drag() =
    runComposeUiTest {
      setContent {
        val scrollState = rememberScrollState()
        val scrollAreaState = rememberScrollAreaState(scrollState)
        ScrollArea(state = scrollAreaState) {
          Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState).testTag("list")) {
            repeat(100) { index ->
              BasicText("Item $index")
            }
          }
          UnstyledVerticalScrollbar(
            scrollAreaState = scrollAreaState,
            modifier = Modifier.testTag("track"),
          ) {
            Thumb(
              modifier = Modifier.testTag("thumb").width(8.dp),
              thumbVisibility = ThumbVisibility.HideWhileIdle(
                enter = EnterTransition.None,
                exit = ExitTransition.None,
                hideDelay = 2.seconds,
              ),
            )
          }
        }
      }

      onNodeWithTag("thumb").assertDoesNotExist()

      // Make thumb visible first.
      onNodeWithTag("list").performTouchInput {
        swipeUp(durationMillis = 1_000)
      }
      onNodeWithTag("thumb").assertIsDisplayed()

      // Start thumb drag and keep pointer down.
      onNodeWithTag("thumb").performTouchInput {
        down(Offset(5f, 5f))
        moveTo(Offset(350f, 350f))
      }
      onNodeWithTag("thumb").assertIsDisplayed()

      // Pointer has moved outside track bounds while drag is still active.

      // Even after hideDelay, thumb must remain visible while drag is active.
      advanceTimeBy(3.seconds)
      waitForIdle()
      onNodeWithTag("thumb").assertIsDisplayed()

      // Release thumb drag.
      onNodeWithTag("thumb").performTouchInput { up() }
      waitForIdle()
      onNodeWithTag("thumb").assertIsDisplayed()

      // Now it may hide after idle delay.
      advanceTimeBy(2.seconds)
      waitForIdle()
      onNodeWithTag("thumb").assertDoesNotExist()
    }

  @Test
  fun vertical_scrollbar_uses_thumb_modifiers_for_minimum_size_and_thickness() = runComposeUiTest {
    setContent {
      val lazyListState = rememberLazyListState()
      val scrollAreaState = rememberScrollAreaState(lazyListState)
      ScrollArea(state = scrollAreaState) {
        LazyColumn(modifier = Modifier.height(200.dp).testTag("list"), state = lazyListState) {
          items((0 until 36_000).toList()) { index ->
            BasicText("Item $index")
          }
        }
        UnstyledVerticalScrollbar(
          scrollAreaState = scrollAreaState,
          modifier = Modifier.testTag("scrollbar"),
        ) {
          Thumb(
            modifier = Modifier
              .testTag("thumb")
              .width(3.dp)
              .height(4.dp),
            thumbVisibility = ThumbVisibility.AlwaysVisible,
          )
        }
      }
    }

    onNodeWithTag("thumb").assertIsDisplayed()
    onNodeWithTag("thumb").assertWidthIsEqualTo(3.dp)
    onNodeWithTag("thumb").assertHeightIsEqualTo(4.dp)
  }
}
