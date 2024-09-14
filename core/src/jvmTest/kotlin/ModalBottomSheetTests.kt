package com.composables.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlinx.coroutines.delay

@OptIn(ExperimentalTestApi::class)
class ModalBottomSheetTests {

    @Test
    fun sheetWithInitialDetentHidden_isNotDisplayed() = runComposeUiTest {
        setContent {
            ModalBottomSheet(rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)) {
                Sheet {
                    Box(Modifier.testTag("sheet_contents").size(40.dp))
                }
            }
        }

        onNodeWithTag("sheet_contents").assertDoesNotExist()
    }

    @Test
    fun sheetWithInitialDetentFullyExpanded_isDisplayed() = runComposeUiTest {
        setContent {
            ModalBottomSheet(rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)) {
                Sheet {
                    Box(Modifier.testTag("sheet_contents").size(40.dp))
                }
            }
        }

        onNodeWithTag("sheet_contents").assertIsDisplayed()
    }


    @Test
    fun settingDetentToFullyDetent_whenInitialIsDetentHidden_isDisplayed() = runComposeUiTest {
        setContent {
            val state = rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)

            LaunchedEffect(Unit) {
                state.currentDetent = SheetDetent.FullyExpanded
            }
            ModalBottomSheet(state) {
                Sheet {
                    Box(Modifier.testTag("sheet_contents").size(40.dp))
                }
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

            val state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)

            ModalBottomSheet(state) {
                Scrim()
                Sheet(Modifier.testTag("sheet")) {
                    Box(Modifier.testTag("sheet_contents").size(contentSize))
                }
            }
        }

        waitForIdle()

        onNodeWithTag("sheet").assertWidthIsEqualTo(150.dp)
        onNodeWithTag("sheet").assertHeightIsEqualTo(150.dp)
    }
}
