package com.composables.core

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat

@OptIn(ExperimentalTestApi::class)
class BottomSheetTest {

    @Test
    fun sheetWithInitialDetentHidden_isNotDisplayed() = runComposeUiTest {
        setContent {
            BottomSheet(
                rememberBottomSheetState(
                    initialDetent = SheetDetent.Hidden, decayAnimationSpec = rememberSplineBasedDecay()
                )
            ) {
                Box(Modifier.testTag("sheet_contents").size(40.dp))
            }
        }

        onNodeWithTag("sheet_contents").assertIsNotDisplayed()
    }

    @Test
    fun sheetWithInitialDetentFullyExpanded_isDisplayed() = runComposeUiTest {
        setContent {
            BottomSheet(
                rememberBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded, decayAnimationSpec = rememberSplineBasedDecay()
                )
            ) {
                Box(Modifier.testTag("sheet_contents").size(40.dp))
            }
        }

        onNodeWithTag("sheet_contents").assertIsDisplayed()
    }


    @Test
    fun settingDetentToFullyDetent_whenInitialIsDetentHidden_isDisplayed() = runComposeUiTest {
        setContent {
            val state = rememberBottomSheetState(
                initialDetent = SheetDetent.Hidden, decayAnimationSpec = rememberSplineBasedDecay()
            )

            LaunchedEffect(Unit) {
                state.targetDetent = SheetDetent.FullyExpanded
            }
            BottomSheet(state) {
                Box(Modifier.testTag("sheet_contents").size(40.dp))
            }
        }

        onNodeWithTag("sheet_contents").assertIsDisplayed()
    }

    @Test
    fun modifyingTheSheetsContent_updatesTheSheetsHeight() = runComposeUiTest {
        setContent {
            var contentSize by remember { mutableStateOf(40.dp) }

            LaunchedEffect(Unit) {
                delay(50)
                contentSize = 150.dp
            }

            val state = rememberBottomSheetState(
                initialDetent = SheetDetent.FullyExpanded, decayAnimationSpec = rememberSplineBasedDecay()
            )

            BottomSheet(state, Modifier.testTag("sheet")) {
                Box(Modifier.testTag("sheet_contents").size(contentSize))
            }
        }

        mainClock.advanceTimeBy(50)
        onNodeWithTag("sheet").assertWidthIsEqualTo(150.dp)
        onNodeWithTag("sheet").assertHeightIsEqualTo(150.dp)
    }

    @Test(expected = IllegalStateException::class)
    fun creatingStateWithNoDetents_throws_exception() = runComposeUiTest {
        setContent {
            rememberBottomSheetState(
                initialDetent = SheetDetent.FullyExpanded, detents = emptyList()
            )
        }
    }

    @Test(expected = IllegalStateException::class)
    fun creating_stateWithoutInitialDetent_throws_exception() = runComposeUiTest {
        setContent {
            rememberBottomSheetState(
                initialDetent = SheetDetent.FullyExpanded, detents = listOf(SheetDetent.Hidden)
            )
        }
    }

    @Test
    fun hiddenOffsetIsZero() = runComposeUiTest {
        var state: BottomSheetState? = null
        setContent {
            state = rememberBottomSheetState(
                initialDetent = SheetDetent.Hidden
            )
            BottomSheet(
                state
            ) {
                Box(Modifier.testTag("sheet_contents").size(40.dp))
            }
        }

        assertThat(state!!.offset).isEqualTo(0f)
    }

    @Test
    fun fullyExpandedOffsetIsEqualToTheHeightOfTheItem() = runComposeUiTest {
        var state: BottomSheetState? = null
        setContent {
            state = rememberBottomSheetState(
                initialDetent = SheetDetent.FullyExpanded
            )
            BottomSheet(state) {
                Box(Modifier.testTag("sheet_contents").size(40.dp))
            }
        }

        assertThat(state!!.offset).isEqualTo(40f)
    }

    @Test
    fun currentAndTargetDetentsUpdateAccordingToSheetPosition() = runComposeUiTest {
        var state: BottomSheetState? = null
        var scope: CoroutineScope? = null
        val settleDuration = 5000

        setContent {
            scope = rememberCoroutineScope()
            state = rememberBottomSheetState(
                initialDetent = SheetDetent.Hidden,
                decayAnimationSpec = rememberSplineBasedDecay(),
                animationSpec = tween(settleDuration),
                detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
            )
            BottomSheet(state) {
                Box(Modifier.testTag("sheet_contents").size(40.dp))
            }
        }

        requireNotNull(state)

        // sheet is idle at Hidden
        assertThat(state.isIdle).isTrue
        assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
        assertThat(state.targetDetent).isEqualTo(SheetDetent.Hidden)

        // sheet is moving towards at FullyExpanded
        scope!!.launch {
            state.animateTo(SheetDetent.FullyExpanded)
        }
        mainClock.advanceTimeBy(1000)
        assertThat(state.isIdle).isFalse
        assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
        assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)

        // sheet is arrived at FullyExpanded
        mainClock.advanceTimeBy(4000)
        assertThat(state.isIdle).isTrue
        assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
        assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)
    }
}
