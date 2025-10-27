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
}
