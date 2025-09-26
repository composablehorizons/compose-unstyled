package com.composables.core

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
class ModalBottomSheetTest {

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
        var state: ModalBottomSheetState? = null
        setContent {
            state = rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)

            LaunchedEffect(Unit) {
                state.targetDetent = SheetDetent.FullyExpanded
            }
            ModalBottomSheet(state) {
                Sheet {
                    Box(Modifier.testTag("sheet_contents").size(40.dp))
                }
            }
        }
        waitUntil { requireNotNull(state).isIdle }
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

        mainClock.advanceTimeBy(50)

        onNodeWithTag("sheet").assertWidthIsEqualTo(150.dp)
        onNodeWithTag("sheet").assertHeightIsEqualTo(150.dp)
    }

    @Test
    fun scrimAnimatesInWhenSheetIsShown() = runComposeUiTest {
        var state: ModalBottomSheetState? = null
        setContent {
            state = rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)

            ModalBottomSheet(state) {
                Scrim(Modifier.testTag("scrim"))
                Sheet {
                    Box(Modifier.testTag("sheet_contents").size(40.dp))
                }
            }
        }

        onNodeWithTag("scrim").assertDoesNotExist()

        runOnIdle {
            requireNotNull(state).targetDetent = SheetDetent.FullyExpanded
        }

        waitUntil { requireNotNull(state).isIdle }
        onNodeWithTag("scrim").assertIsDisplayed()
    }
}
