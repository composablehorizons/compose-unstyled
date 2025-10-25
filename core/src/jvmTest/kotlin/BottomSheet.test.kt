package com.composeunstyled

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.composables.core.BottomSheet
import com.composables.core.BottomSheetState
import com.composables.core.SheetDetent
import com.composables.core.rememberBottomSheetState
import kotlin.test.Test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

@OptIn(ExperimentalTestApi::class)
class BottomSheetTest {

    // invalid state cases
    @Test(expected = IllegalStateException::class)
    fun throwsException_whenCreatingState_withoutDetents() = runComposeUiTest {
        setContent {
            BottomSheetState(
                initialDetent = SheetDetent.FullyExpanded,
                detents = emptyList(),
                coroutineScope = rememberCoroutineScope(),
                animationSpec = tween(),
                density = { density },
                velocityThreshold = { 0f },
                positionalThreshold = { 0f },
                confirmDetentChange = { true },
                decayAnimationSpec = rememberSplineBasedDecay()
            )
        }
    }

    @Test(expected = IllegalStateException::class)
    fun throwsException_whenCreatingState_andInitialDetentIsNotPartOfDetents() = runComposeUiTest {
        val customDetent = SheetDetent("custom") { _, _ -> 0.dp }
        setContent {
            BottomSheetState(
                initialDetent = customDetent,
                detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
                coroutineScope = rememberCoroutineScope(),
                animationSpec = tween(),
                density = { density },
                velocityThreshold = { 0f },
                positionalThreshold = { 0f },
                confirmDetentChange = { true },
                decayAnimationSpec = rememberSplineBasedDecay()
            )
        }
    }

    @Test(expected = IllegalStateException::class)
    fun throwsException_whenCreatingState_andHasDuplicateDetents() = runComposeUiTest {
        setContent {
            BottomSheetState(
                initialDetent = SheetDetent.Hidden,
                detents = listOf(SheetDetent.Hidden, SheetDetent.Hidden),
                coroutineScope = rememberCoroutineScope(),
                animationSpec = tween(),
                density = { density },
                velocityThreshold = { 0f },
                positionalThreshold = { 0f },
                confirmDetentChange = { true },
                decayAnimationSpec = rememberSplineBasedDecay()
            )
        }
    }

    //
    //

    @Test
    fun sheetWithInitialDetentHidden_isNotDisplayed() = runComposeUiTest {
        setContent {
            BottomSheet(
                rememberBottomSheetState(
                    initialDetent = SheetDetent.Hidden
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
                    initialDetent = SheetDetent.FullyExpanded, 
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
                initialDetent = SheetDetent.Hidden, 
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
                initialDetent = SheetDetent.FullyExpanded, 
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
        runBlocking {

            var state: BottomSheetState? = null
            var scope: CoroutineScope? = null
            val settleDuration = 5000L

            setContent {
                scope = rememberCoroutineScope()
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    animationSpec = tween(settleDuration.toInt()),
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )
                BottomSheet(state) {
                    Box(Modifier.testTag("sheet_contents").size(40.dp))
                }
            }

            requireNotNull(state)
            requireNotNull(scope)

            // sheet is idle at Hidden
            assertThat(state.isIdle).isTrue
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
            assertThat(state.targetDetent).isEqualTo(SheetDetent.Hidden)

            // sheet starting moving towards at FullyExpanded
            scope.launch {
                state.animateTo(SheetDetent.FullyExpanded)
            }
            mainClock.advanceTimeBy(1000L)

            assertThat(state.isIdle).isFalse
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
            assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)

            // sheet is close towards at FullyExpanded
            mainClock.advanceTimeBy(3000L)
            assertThat(state.isIdle).isFalse
            assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
            assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)

            // Wait for sheet to settle
            awaitIdle()
            assertThat(state.isIdle).isTrue
            assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
            assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)
        }
    }

    @Test
    fun invalidateDetentsUpdatesDetents() = runComposeUiTest {
        var state: BottomSheetState? = null
        var detentHeight by mutableStateOf(50.dp)
        val dynamicDetent = SheetDetent("dynamic") { _, _ ->
            detentHeight
        }
        setContent {
            state = rememberBottomSheetState(
                initialDetent = dynamicDetent,
                detents = listOf(dynamicDetent)
            )
            BottomSheet(state) {
                Column(Modifier.testTag("sheet_contents").size(100.dp)) {
                    Text("Top")
                    Spacer(Modifier.weight(1f))
                    Text("Bottom")
                }
            }
        }
        onNodeWithText("Bottom").assertIsNotDisplayed()
        detentHeight += 50.dp
        state!!.invalidateDetents()
        onNodeWithText("Bottom").assertIsDisplayed()
    }

}
