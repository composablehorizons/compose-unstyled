package com.composables.core

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlinx.coroutines.delay

@OptIn(ExperimentalTestApi::class)
class BottomSheetTests {

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
                state.currentDetent = SheetDetent.FullyExpanded
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

        waitForIdle()
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
}
