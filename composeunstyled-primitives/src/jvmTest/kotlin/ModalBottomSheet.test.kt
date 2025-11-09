package com.composeunstyled

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.composables.core.*
import kotlin.test.Test
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat

@OptIn(ExperimentalTestApi::class)
class ModalBottomSheetTest {
    @Test
    fun `initial state`() = runTestSuite {
        testCase("sheet is visible, when initial detent is fully expanded") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.testTag("sheet").size(40.dp)) }
                }
            }
            onNodeWithTag("sheet").assertExists()
        }

        testCase("sheet is not visible, when initial detent is hidden") {
            setContent {
                ModalBottomSheet(rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)) {
                    Scrim()
                    Sheet { Box(Modifier.testTag("sheet").size(40.dp)) }
                }
            }
            onNodeWithTag("sheet").assertDoesNotExist()
        }

        testCase("sheet is rested at the height of its content, when initial detent is fully expanded") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.testTag("sheet").size(40.dp)) }
                }
            }
            onNodeWithTag("sheet").assertExists()
            assertThat(state.offset).isEqualTo(40f)
        }


        testCase("scrim is visible, when initial detent is fully expanded") {
            setContent {
                ModalBottomSheet(rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }
            onNodeWithTag("scrim").assertExists()
        }
    }


    @Test
    fun enters() = runTestSuite {
        testCase("dialog exists, when going from hidden to visible") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }
            onNode(isDialog()).assertDoesNotExist()
            state.targetDetent = SheetDetent.FullyExpanded
            waitForIdle()
            onNode(isDialog()).assertExists()
        }

        testCase("sheet rests at the correct height,after entering") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }

            state.targetDetent = SheetDetent.FullyExpanded
            waitForIdle()
            assertThat(state.offset).isEqualTo(40f)
        }

        testCase("animates sheet in, when entering") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet {
                        Box(Modifier.testTag("sheet").size(5000.dp))
                    }
                }
            }
            mainClock.autoAdvance = false
            onNodeWithTag("sheet").assertDoesNotExist()

            // start animation
            // we should now be in motion
            state.targetDetent = SheetDetent.FullyExpanded
            mainClock.advanceTimeBy(1)
            assertThat(state.isIdle).isFalse
            onNodeWithTag("sheet").assertExists()

            // finish animation. we should be resting
            mainClock.autoAdvance = true
            awaitIdle()
            assertThat(state.bottomSheetState.isIdle).isTrue
        }

        testCase("animates scrim in, when entering") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)
                ModalBottomSheet(state) {
                    Scrim(
                        modifier = Modifier.testTag("scrim"),
                        enter = fadeIn(tween(durationMillis = 300))
                    )
                    Sheet { Box(Modifier.testTag("sheet").size(40.dp)) }
                }
            }
            onNodeWithTag("scrim").assertDoesNotExist()

            state.targetDetent = SheetDetent.FullyExpanded

            // we need to wait for the modal to enter first
            waitUntil { state.modalIsAdded }
            mainClock.autoAdvance = false
            mainClock.advanceTimeBy(1)
            // Scrim should start appearing now

            // Check that the scrim animation is running (not instantly completed)
            onNodeWithTag("scrim").assertExists()
            assertThat(state.scrimState.isIdle).isFalse()

            // Advance halfway through animation
            mainClock.advanceTimeBy(150)
            assertThat(state.scrimState.isIdle).isFalse()

            // Complete the animation
            mainClock.advanceTimeBy(150)
            waitForIdle()
            assertThat(state.scrimState.isIdle).isTrue()
            assertThat(state.scrimState.currentState).isTrue()
        }
    }

    @Test
    fun exits() = runTestSuite {
        testCase("waits until exit animation ends before removing dialog") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }
            onNode(isDialog()).assertExists()
            state.targetDetent = SheetDetent.Hidden
            waitForIdle()
            onNode(isDialog()).assertDoesNotExist()
        }

        testCase("starts sheet exit animation, when exiting") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.testTag("sheet").size(40.dp)) }
                }
            }
            onNodeWithTag("sheet").assertExists()
            state.targetDetent = SheetDetent.Hidden
            waitForIdle()
            onNodeWithTag("sheet").assertDoesNotExist()
        }

        testCase("starts scrim exit animation, when exiting") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
                ModalBottomSheet(state) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }
            onNodeWithTag("scrim").assertExists()
            state.targetDetent = SheetDetent.Hidden
            waitForIdle()
            onNodeWithTag("scrim").assertDoesNotExist()
        }
    }

    @Test
    fun `dismiss interactions`() = runTestSuite {
        testCase("sheet is dismissed, when tapping outside with dismissOnClickOutside true") {
            var dismissCalled = false
            setContent {
                ModalBottomSheet(
                    state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
                    properties = ModalSheetProperties(dismissOnClickOutside = true),
                    onDismiss = { dismissCalled = true }
                ) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }
            onNodeWithTag("scrim").performClick()
            assertThat(dismissCalled).isTrue
        }

        testCase("sheet is not dismissed, when tapping outside with dismissOnClickOutside false") {
            var dismissCalled = false
            setContent {
                ModalBottomSheet(
                    rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
                    properties = ModalSheetProperties(dismissOnClickOutside = false),
                    onDismiss = { dismissCalled = true }
                ) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }
            onNodeWithTag("scrim").performClick()
            assertThat(dismissCalled).isFalse
        }

        testCase("modal is dismissed, when clicking outside") {
            var dismissCalled = false
            setContent {
                ModalBottomSheet(
                    state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
                    properties = ModalSheetProperties(dismissOnClickOutside = true),
                    onDismiss = { dismissCalled = true }
                ) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }
            onNodeWithTag("scrim").performClick()
            assertThat(dismissCalled).isTrue
        }

        testCase("modal is dismissed, when sheet is dismissed programmatically") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
                ModalBottomSheet(state) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }
            onNodeWithTag("scrim").assertExists()
            onNode(isDialog()).assertExists()

            // Dismiss the sheet programmatically by setting target to Hidden
            state.targetDetent = SheetDetent.Hidden
            waitForIdle()

            // Modal should be removed once sheet reaches Hidden and is idle
            onNode(isDialog()).assertDoesNotExist()
            onNodeWithTag("scrim").assertDoesNotExist()
        }

        testCase("onDismiss is called, when sheet is swiped to hidden") {
            lateinit var state: ModalBottomSheetState
            var dismissCalled = false
            setContent {
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(state = state, onDismiss = { dismissCalled = true }) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
                }
            }
            onNodeWithTag("scrim").assertExists()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)

            // Swipe the sheet down to dismiss
            onNodeWithTag("sheet").performTouchInput {
                swipeDown()
            }
            waitForIdle()

            // Verify onDismiss was called when sheet reached Hidden
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
            assertThat(dismissCalled).isTrue()
        }

        testCase("modal is dismissed, when sheet is swiped to hidden") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(state) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
                }
            }
            onNode(isDialog()).assertExists()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)

            // Swipe the sheet down to dismiss
            onNodeWithTag("sheet").performTouchInput {
                swipeDown()
            }
            waitForIdle()

            // Verify sheet reached Hidden and modal was removed
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
            onNode(isDialog()).assertDoesNotExist()
            onNodeWithTag("scrim").assertDoesNotExist()
        }
    }

    @Test
    fun `state changes during interactions`() = runTestSuite {
        testCase("currentDetent updates to Hidden, when targetDetent changes to Hidden during opening animation") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
                    animationSpec = tween(2000)
                )
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.testTag("sheet").size(100.dp)) }
                }
            }

            waitForIdle()
            mainClock.autoAdvance = false

            // Start opening animation
            state.targetDetent = SheetDetent.FullyExpanded
            mainClock.advanceTimeBy(500)

            // Change target to Hidden mid-animation
            state.targetDetent = SheetDetent.Hidden

            mainClock.autoAdvance = true
            waitForIdle()

            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
        }

        testCase("isIdle becomes true, when animation to Hidden completes") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
                    animationSpec = tween(1000)
                )
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.size(100.dp)) }
                }
            }

            waitForIdle()
            assertThat(state.isIdle).isTrue()

            mainClock.autoAdvance = false

            // Start closing animation
            state.targetDetent = SheetDetent.Hidden
            mainClock.advanceTimeBy(500)

            // Should not be idle during animation
            assertThat(state.isIdle).isFalse()

            // Complete animation
            mainClock.autoAdvance = true
            waitForIdle()

            // Should be idle after animation completes
            assertThat(state.isIdle).isTrue()
        }

        testCase("currentDetent updates to Hidden, when sheet caught and dismissed with gesture during opening animation") {
            lateinit var state: ModalBottomSheetState
            setContent {
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
                    animationSpec = tween(2000)
                )
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.testTag("sheet").size(300.dp)) }
                }
            }

            waitForIdle()
            mainClock.autoAdvance = false

            // Start opening animation programmatically
            state.targetDetent = SheetDetent.FullyExpanded
            mainClock.advanceTimeBy(500)

            // Catch the sheet mid-animation and swipe it down to dismiss (gesture-driven)
            onNodeWithTag("sheet").performTouchInput {
                swipeDown()
            }

            mainClock.autoAdvance = true
            waitForIdle()

            // currentDetent should update to Hidden immediately after gesture completes
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
        }
    }

    @Test
    fun animateTo() = runTestSuite {
        testCase("modal appears and sheet expands to FullyExpanded, when animating from Hidden") {
            lateinit var state: ModalBottomSheetState
            lateinit var scope: kotlinx.coroutines.CoroutineScope

            setContent {
                scope = androidx.compose.runtime.rememberCoroutineScope()
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(state) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
                }
            }

            waitForIdle()
            onNode(isDialog()).assertDoesNotExist()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)

            scope.launch {
                state.animateTo(SheetDetent.FullyExpanded)
            }
            waitForIdle()

            onNode(isDialog()).assertExists()
            onNodeWithTag("sheet").assertIsDisplayed()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
            assertThat(state.offset).isEqualTo(600f)
        }

        testCase("modal disappears and sheet collapses to Hidden, when animating from FullyExpanded") {
            lateinit var state: ModalBottomSheetState
            lateinit var scope: kotlinx.coroutines.CoroutineScope

            setContent {
                scope = androidx.compose.runtime.rememberCoroutineScope()
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(state) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
                }
            }

            waitForIdle()
            onNode(isDialog()).assertExists()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)

            scope.launch {
                state.animateTo(SheetDetent.Hidden)
            }
            waitForIdle()

            onNode(isDialog()).assertDoesNotExist()
            onNodeWithTag("sheet").assertDoesNotExist()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
        }

        testCase("sheet moves to custom detent, when animating from Hidden to custom detent") {
            val customDetent = SheetDetent("custom") { _, _ -> 300.dp }
            lateinit var state: ModalBottomSheetState
            lateinit var scope: kotlinx.coroutines.CoroutineScope

            setContent {
                scope = androidx.compose.runtime.rememberCoroutineScope()
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    detents = listOf(SheetDetent.Hidden, customDetent, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
                }
            }

            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)

            scope.launch {
                state.animateTo(customDetent)
            }
            waitForIdle()

            onNode(isDialog()).assertExists()
            assertThat(state.currentDetent).isEqualTo(customDetent)
            assertThat(state.offset).isEqualTo(300f)
        }

        testCase("target detent updates during animation, when animating to FullyExpanded") {
            lateinit var state: ModalBottomSheetState
            lateinit var scope: kotlinx.coroutines.CoroutineScope
            val settleDuration = 5000L

            setContent {
                scope = androidx.compose.runtime.rememberCoroutineScope()
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    animationSpec = tween(settleDuration.toInt()),
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
                }
            }

            waitForIdle()
            mainClock.autoAdvance = false

            scope.launch {
                state.animateTo(SheetDetent.FullyExpanded)
            }
            mainClock.advanceTimeBy(1000L)

            // During animation, target should be FullyExpanded
            assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)
            assertThat(state.isIdle).isFalse()
        }

        testCase("animateTo behaves like targetDetent setter, when animating from Hidden to FullyExpanded") {
            lateinit var state: ModalBottomSheetState
            lateinit var scope: kotlinx.coroutines.CoroutineScope

            setContent {
                scope = androidx.compose.runtime.rememberCoroutineScope()
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
                }
            }

            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)

            scope.launch {
                state.animateTo(SheetDetent.FullyExpanded)
            }
            waitForIdle()

            // Should reach FullyExpanded
            assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
            assertThat(state.offset).isEqualTo(600f)
            onNode(isDialog()).assertExists()
        }
    }

    @Test
    fun jumpTo() = runTestSuite {
        testCase("modal appears and sheet jumps to FullyExpanded immediately, when jumping from Hidden") {
            lateinit var state: ModalBottomSheetState
            lateinit var scope: kotlinx.coroutines.CoroutineScope

            setContent {
                scope = androidx.compose.runtime.rememberCoroutineScope()
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
                }
            }

            waitForIdle()
            onNode(isDialog()).assertDoesNotExist()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
            assertThat(state.offset).isEqualTo(0f)

            scope.launch {
                state.jumpTo(SheetDetent.FullyExpanded)
            }
            waitForIdle()

            onNode(isDialog()).assertExists()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
            assertThat(state.offset).isEqualTo(600f)
            assertThat(state.isIdle).isTrue()
            onNodeWithTag("sheet").assertIsDisplayed()
        }

        testCase("modal disappears and sheet jumps to Hidden immediately, when jumping from FullyExpanded") {
            lateinit var state: ModalBottomSheetState
            lateinit var scope: kotlinx.coroutines.CoroutineScope

            setContent {
                scope = androidx.compose.runtime.rememberCoroutineScope()
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
                }
            }

            waitForIdle()
            onNode(isDialog()).assertExists()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)

            scope.launch {
                state.jumpTo(SheetDetent.Hidden)
            }
            waitForIdle()

            onNode(isDialog()).assertDoesNotExist()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
            assertThat(state.offset).isEqualTo(0f)
            assertThat(state.isIdle).isTrue()
        }

        testCase("sheet jumps to custom detent immediately, when jumping from Hidden") {
            val customDetent = SheetDetent("custom") { _, _ -> 300.dp }
            lateinit var state: ModalBottomSheetState
            lateinit var scope: kotlinx.coroutines.CoroutineScope

            setContent {
                scope = androidx.compose.runtime.rememberCoroutineScope()
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    detents = listOf(SheetDetent.Hidden, customDetent, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
                }
            }

            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)

            scope.launch {
                state.jumpTo(customDetent)
            }
            waitForIdle()

            onNode(isDialog()).assertExists()
            assertThat(state.currentDetent).isEqualTo(customDetent)
            assertThat(state.offset).isEqualTo(300f)
            assertThat(state.isIdle).isTrue()
        }

        testCase("jumpTo changes state without animation, when jumping between detents") {
            val halfDetent = SheetDetent("half") { _, _ -> 300.dp }
            lateinit var state: ModalBottomSheetState
            lateinit var scope: kotlinx.coroutines.CoroutineScope

            setContent {
                scope = androidx.compose.runtime.rememberCoroutineScope()
                state = rememberModalBottomSheetState(
                    initialDetent = halfDetent,
                    detents = listOf(SheetDetent.Hidden, halfDetent, SheetDetent.FullyExpanded),
                    animationSpec = tween(5000) // Long animation to verify jumpTo is instant
                )
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.size(600.dp)) }
                }
            }

            waitForIdle()
            mainClock.autoAdvance = false

            scope.launch {
                state.jumpTo(SheetDetent.FullyExpanded)
            }
            mainClock.advanceTimeByFrame()

            // Should be at FullyExpanded immediately, without animation
            assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
            assertThat(state.offset).isEqualTo(600f)
            assertThat(state.isIdle).isTrue()
        }

        testCase("modal remains visible, when jumping between non-Hidden detents") {
            val halfDetent = SheetDetent("half") { _, _ -> 300.dp }
            lateinit var state: ModalBottomSheetState
            lateinit var scope: kotlinx.coroutines.CoroutineScope

            setContent {
                scope = androidx.compose.runtime.rememberCoroutineScope()
                state = rememberModalBottomSheetState(
                    initialDetent = halfDetent,
                    detents = listOf(SheetDetent.Hidden, halfDetent, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(state) {
                    Scrim()
                    Sheet { Box(Modifier.size(600.dp)) }
                }
            }

            waitForIdle()
            onNode(isDialog()).assertExists()
            assertThat(state.currentDetent).isEqualTo(halfDetent)

            scope.launch {
                state.jumpTo(SheetDetent.FullyExpanded)
            }
            waitForIdle()

            // Modal should still be visible
            onNode(isDialog()).assertExists()
            assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
        }
    }
}
