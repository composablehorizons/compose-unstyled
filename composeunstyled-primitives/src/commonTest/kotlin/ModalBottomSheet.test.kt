package com.composeunstyled

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.unit.dp
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalBottomSheetState
import com.composables.core.ModalSheetProperties
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ModalBottomSheetTest {
    @Test
    fun initial_state() = runTestSuite {
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
            assertEquals(40f, state.offset)
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
            assertEquals(40f, state.offset)
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
            assertFalse(state.isIdle)
            onNodeWithTag("sheet").assertExists()

            // finish animation. we should be resting
            mainClock.autoAdvance = true
            awaitIdle()
            assertTrue(state.bottomSheetState.isIdle)
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
            assertFalse(state.scrimState.isIdle)

            // Advance halfway through animation
            mainClock.advanceTimeBy(150)
            assertFalse(state.scrimState.isIdle)

            // Complete the animation
            mainClock.advanceTimeBy(150)
            waitForIdle()
            assertTrue(state.scrimState.isIdle)
            assertTrue(state.scrimState.currentState)
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
    fun dismiss_interactions() = runTestSuite {
        testCase("sheet is dismissed, when tapping outside with dismissOnClickOutside true") {
            setContent {
                ModalBottomSheet(
                    state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
                    properties = ModalSheetProperties(dismissOnClickOutside = true)
                ) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }
            onNodeWithTag("scrim").performClick()
            waitForIdle()
            onNode(isDialog()).assertDoesNotExist()
        }

        testCase("sheet is not dismissed, when tapping outside with dismissOnClickOutside false") {
            setContent {
                ModalBottomSheet(
                    rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
                    properties = ModalSheetProperties(dismissOnClickOutside = false)
                ) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }
            onNodeWithTag("scrim").performClick()
            waitForIdle()
            onNode(isDialog()).assertExists()
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

            state.targetDetent = SheetDetent.Hidden
            waitForIdle()

            onNode(isDialog()).assertDoesNotExist()
            onNodeWithTag("scrim").assertDoesNotExist()
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
            assertEquals(SheetDetent.FullyExpanded, state.currentDetent)

            onNodeWithTag("sheet").performTouchInput {
                swipeDown()
            }
            waitForIdle()

            assertEquals(SheetDetent.Hidden, state.currentDetent)
            onNode(isDialog()).assertDoesNotExist()
            onNodeWithTag("scrim").assertDoesNotExist()
        }
    }

    @Test
    fun state_changes_during_interactions() = runTestSuite {
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

            assertEquals(SheetDetent.Hidden, state.currentDetent)
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
            assertTrue(state.isIdle)

            mainClock.autoAdvance = false

            // Start closing animation
            state.targetDetent = SheetDetent.Hidden
            mainClock.advanceTimeBy(500)

            // Should not be idle during animation
            assertFalse(state.isIdle)

            // Complete animation
            mainClock.autoAdvance = true
            waitForIdle()

            // Should be idle after animation completes
            assertTrue(state.isIdle)
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
            assertEquals(SheetDetent.Hidden, state.currentDetent)
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
            assertEquals(SheetDetent.Hidden, state.currentDetent)

            scope.launch {
                state.animateTo(SheetDetent.FullyExpanded)
            }
            waitForIdle()

            onNode(isDialog()).assertExists()
            onNodeWithTag("sheet").assertIsDisplayed()
            assertEquals(SheetDetent.FullyExpanded, state.currentDetent)
            assertEquals(600f, state.offset)
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
            assertEquals(SheetDetent.FullyExpanded, state.currentDetent)

            scope.launch {
                state.animateTo(SheetDetent.Hidden)
            }
            waitForIdle()

            onNode(isDialog()).assertDoesNotExist()
            onNodeWithTag("sheet").assertDoesNotExist()
            assertEquals(SheetDetent.Hidden, state.currentDetent)
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
            assertEquals(SheetDetent.Hidden, state.currentDetent)

            scope.launch {
                state.animateTo(customDetent)
            }
            waitForIdle()

            onNode(isDialog()).assertExists()
            assertEquals(customDetent, state.currentDetent)
            assertEquals(300f, state.offset)
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
            assertEquals(SheetDetent.FullyExpanded, state.targetDetent)
            assertFalse(state.isIdle)
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
            assertEquals(SheetDetent.Hidden, state.currentDetent)

            scope.launch {
                state.animateTo(SheetDetent.FullyExpanded)
            }
            waitForIdle()

            // Should reach FullyExpanded
            assertEquals(SheetDetent.FullyExpanded, state.currentDetent)
            assertEquals(600f, state.offset)
            onNode(isDialog()).assertExists()
        }
    }

    @Test
    fun onDismiss() = runTestSuite {
        testCase("given sheet is fully expanded and dismissOnClickOutside is true, when tapping outside, then onDismiss is called") {
            var dismissCallCount = 0
            setContent {
                ModalBottomSheet(
                    state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
                    properties = ModalSheetProperties(dismissOnClickOutside = true),
                    onDismiss = { dismissCallCount++ }
                ) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }
            onNodeWithTag("scrim").performClick()
            waitForIdle()
            assertEquals(1, dismissCallCount)
        }

        testCase("given sheet is fully expanded and dismissOnClickOutside is false, when tapping outside, then onDismiss is not called") {
            var dismissCallCount = 0
            setContent {
                ModalBottomSheet(
                    state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
                    properties = ModalSheetProperties(dismissOnClickOutside = false),
                    onDismiss = { dismissCallCount++ }
                ) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }
            onNodeWithTag("scrim").performClick()
            waitForIdle()
            assertEquals(0, dismissCallCount)
        }

        testCase("given sheet is fully expanded, when sheet is swiped to hidden, then onDismiss is called") {
            lateinit var state: ModalBottomSheetState
            var dismissCallCount = 0
            setContent {
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(state = state, onDismiss = { dismissCallCount++ }) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
                }
            }
            onNodeWithTag("scrim").assertExists()

            onNodeWithTag("sheet").performTouchInput {
                swipeDown()
            }

            waitForIdle()

            assertEquals(SheetDetent.Hidden, state.currentDetent)
            assertEquals(1, dismissCallCount)
        }

        testCase("given sheet started hidden and was shown, when clicking outside, then onDismiss is called") {
            lateinit var state: ModalBottomSheetState
            var dismissCallCount = 0
            setContent {
                state = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )
                ModalBottomSheet(
                    state = state,
                    properties = ModalSheetProperties(dismissOnClickOutside = true),
                    onDismiss = { dismissCallCount++ }
                ) {
                    Scrim(Modifier.testTag("scrim"))
                    Sheet { Box(Modifier.size(40.dp)) }
                }
            }

            // Show the sheet
            state.targetDetent = SheetDetent.FullyExpanded
            waitForIdle()
            onNode(isDialog()).assertExists()

            // Dismiss by clicking outside
            println("--- CLICKING OUTSIDE")
            onNodeWithTag("scrim").performClick()

            waitForIdle()
            println("--- ASSERTING")
            assertEquals(SheetDetent.Hidden, state.currentDetent)
            assertEquals(1, dismissCallCount)
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
            assertEquals(SheetDetent.Hidden, state.currentDetent)
            assertEquals(0f, state.offset)

            scope.launch {
                state.jumpTo(SheetDetent.FullyExpanded)
            }
            waitForIdle()

            onNode(isDialog()).assertExists()
            assertEquals(SheetDetent.FullyExpanded, state.currentDetent)
            assertEquals(600f, state.offset)
            assertTrue(state.isIdle)
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
            assertEquals(SheetDetent.FullyExpanded, state.currentDetent)

            scope.launch {
                state.jumpTo(SheetDetent.Hidden)
            }
            waitForIdle()

            onNode(isDialog()).assertDoesNotExist()
            assertEquals(SheetDetent.Hidden, state.currentDetent)
            assertEquals(0f, state.offset)
            assertTrue(state.isIdle)
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
            assertEquals(SheetDetent.Hidden, state.currentDetent)

            scope.launch {
                state.jumpTo(customDetent)
            }
            waitForIdle()

            onNode(isDialog()).assertExists()
            assertEquals(customDetent, state.currentDetent)
            assertEquals(300f, state.offset)
            assertTrue(state.isIdle)
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
            assertEquals(SheetDetent.FullyExpanded, state.currentDetent)
            assertEquals(600f, state.offset)
            assertTrue(state.isIdle)
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
            assertEquals(halfDetent, state.currentDetent)

            scope.launch {
                state.jumpTo(SheetDetent.FullyExpanded)
            }
            waitForIdle()

            // Modal should still be visible
            onNode(isDialog()).assertExists()
            assertEquals(SheetDetent.FullyExpanded, state.currentDetent)
        }
    }
}
