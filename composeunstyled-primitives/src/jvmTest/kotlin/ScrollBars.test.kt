package com.composeunstyled

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import com.composables.core.ScrollArea
import com.composables.core.Thumb
import com.composables.core.ThumbVisibility
import com.composables.core.VerticalScrollbar
import com.composables.core.rememberScrollAreaState
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTestApi::class)
class ScrollBarsTest {

    @Test
    fun `AlwaysVisible keeps the thumb visible`() = runComposeUiTest {
        setContent {
            val scrollState = rememberScrollState()
            ScrollArea(state = rememberScrollAreaState(scrollState)) {
                Column(modifier = Modifier.height(200.dp)) {
                    repeat(100) { index ->
                        Text("Item $index")
                    }
                }
                VerticalScrollbar(modifier = Modifier.testTag("scrollbar")) {
                    Thumb(
                        modifier = Modifier.testTag("thumb"),
                        thumbVisibility = ThumbVisibility.AlwaysVisible
                    )
                }
            }
        }

        onNodeWithTag("thumb").assertIsDisplayed()
        advanceTimeBy(60.seconds)
        onNodeWithTag("thumb").assertIsDisplayed()
    }


    @Test
    fun `HideWhileIdle starts with thumb out of composition`() = runComposeUiTest {
        setContent {
            val scrollState = rememberScrollState()
            ScrollArea(state = rememberScrollAreaState(scrollState)) {
                Column(modifier = Modifier.height(200.dp)) {
                    repeat(100) { index ->
                        Text("Item $index")
                    }
                }
                VerticalScrollbar(modifier = Modifier.testTag("scrollbar")) {
                    Thumb(
                        modifier = Modifier.testTag("thumb"),
                        thumbVisibility = ThumbVisibility.HideWhileIdle(
                            enter = AppearInstantly,
                            exit = DisappearInstantly,
                            hideDelay = 5.seconds
                        )
                    )
                }
            }
        }

        onNodeWithTag("thumb").assertDoesNotExist()
    }

    @Test
    fun `HideWhileIdle shows thumb when list is scrolled`() = runComposeUiTest {
        setContent {
            val scrollState = rememberScrollState()
            ScrollArea(state = rememberScrollAreaState(scrollState)) {
                Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState).testTag("list")) {
                    repeat(100) { index ->
                        Text("Item $index")
                    }
                }
                VerticalScrollbar(modifier = Modifier.testTag("scrollbar")) {
                    Thumb(
                        modifier = Modifier.testTag("thumb"),
                        thumbVisibility = ThumbVisibility.HideWhileIdle(
                            enter = AppearInstantly,
                            exit = DisappearInstantly,
                            hideDelay = 5.seconds
                        )
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
    fun `HideWhileIdle hides thumb after given time after done scrolling`() = runComposeUiTest {
        // TODO this checks for drags, not scrolls
        setContent {
            val scrollState = rememberScrollState()
            ScrollArea(
                state = rememberScrollAreaState(scrollState)
            ) {
                Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState).testTag("list")) {
                    repeat(100) { index ->
                        Text("Item $index")
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.testTag("scrollbar")
                        .align(Alignment.CenterEnd)
                ) {
                    Thumb(
                        modifier = Modifier.testTag("thumb"),
                        thumbVisibility = ThumbVisibility.HideWhileIdle(
                            enter = AppearInstantly,
                            exit = DisappearInstantly,
                            hideDelay = 5.seconds
                        )
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
    fun `HideWhileIdle does not hide thumb while dragging`() = runComposeUiTest {
        setContent {
            val scrollState = rememberScrollState()
            ScrollArea(state = rememberScrollAreaState(scrollState)) {
                Column(
                    modifier = Modifier
                        .height(200.dp)
                        .verticalScroll(scrollState)
                        .testTag("list")
                ) {
                    repeat(100) { Text("Item $it") }
                }
                VerticalScrollbar(
                    modifier = Modifier
                        .testTag("scrollbar")
                        .align(Alignment.CenterEnd)
                ) {
                    Thumb(
                        modifier = Modifier.testTag("thumb"),
                        thumbVisibility = ThumbVisibility.HideWhileIdle(
                            enter = AppearInstantly,
                            exit = DisappearInstantly,
                            hideDelay = 2.seconds
                        )
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
    fun `HideWhileIdle does not hide thumb while track is hovered`() = runComposeUiTest {
        setContent {
            val scrollState = rememberScrollState()
            ScrollArea(state = rememberScrollAreaState(scrollState)) {
                Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState).testTag("list")) {
                    repeat(100) { index ->
                        Text("Item $index")
                    }
                }
                VerticalScrollbar(modifier = Modifier.testTag("track")) {
                    Thumb(
                        modifier = Modifier.testTag("thumb"),
                        thumbVisibility = ThumbVisibility.HideWhileIdle(
                            enter = AppearInstantly,
                            exit = DisappearInstantly,
                            hideDelay = 5.seconds
                        )
                    )
                }
            }
        }

        onNodeWithTag("thumb").assertDoesNotExist()

        onNodeWithTag("track").performMouseInput { enter() }
        onNodeWithTag("track").performMouseInput { exit() }
        onNodeWithTag("thumb").assertDoesNotExist()


        // Hover on track
        onNodeWithTag("list").performTouchInput {
            swipeUp(durationMillis = 1_000)
        }
        onNodeWithTag("track").performMouseInput { enter() }
        onNodeWithTag("thumb").assertIsDisplayed()

        // Advance time beyond hideDelay while hovered
        advanceTimeBy(6.seconds)
        onNodeWithTag("thumb").assertIsDisplayed() // Should still be visible

        // Unhover
        onNodeWithTag("track").performMouseInput { exit() }

        // Advance time by hideDelay
        advanceTimeBy(5.seconds)
        onNodeWithTag("thumb").assertDoesNotExist()
    }

    @Test
    fun `HideWhileIdle does not hide thumb while dragging the thumb`() = runComposeUiTest {
        setContent {
            val scrollState = rememberScrollState()
            ScrollArea(state = rememberScrollAreaState(scrollState)) {
                Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState).testTag("list")) {
                    repeat(100) { index ->
                        Text("Item $index")
                    }
                }
                VerticalScrollbar(modifier = Modifier.testTag("scrollbar")) {
                    Thumb(
                        modifier = Modifier.testTag("thumb"),
                        thumbVisibility = ThumbVisibility.HideWhileIdle(
                            enter = AppearInstantly,
                            exit = DisappearInstantly,
                            hideDelay = 5.seconds
                        )
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

        // Advance time by hideDelay
        advanceTimeBy(5.seconds)
        onNodeWithTag("thumb").assertDoesNotExist()
    }
}
