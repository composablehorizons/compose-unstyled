package com.composables.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.espresso.Espresso
import com.composeunstyled.runTestSuite
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ModalBottomSheet {

    @Test
    fun backPress() = runTestSuite {
        testCase("sheet is dismissed, when pressing Back with dismissOnBackPress true") {
            var dismissCalled = false
            setContent {
                ModalBottomSheet(
                    state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
                    properties = ModalSheetProperties(dismissOnBackPress = true),
                    onDismiss = { dismissCalled = true }
                ) {
                    Scrim()
                    Sheet(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        Box(
                            Modifier
                                .testTag("sheet")
                                .size(40.dp)
                        )
                    }
                }
            }

            onNodeWithTag("sheet").assertExists()
            Espresso.pressBack()
            onNodeWithTag("sheet").assertDoesNotExist()
            assertTrue(dismissCalled)
        }

        testCase("sheet is not dismissed, when pressing escape with dismissOnBackPress false") {
            var dismissCalled = false
            setContent {
                ModalBottomSheet(
                    rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
                    properties = ModalSheetProperties(dismissOnBackPress = false),
                    onDismiss = { dismissCalled = true }
                ) {
                    Scrim()
                    Sheet {
                        Box(
                            Modifier
                                .testTag("sheet")
                                .size(40.dp)
                        )
                    }
                }
            }
            onNodeWithTag("sheet").assertExists()
            Espresso.pressBack()
            onNodeWithTag("sheet").assertExists()
            assertFalse(dismissCalled)
        }

    }
}
