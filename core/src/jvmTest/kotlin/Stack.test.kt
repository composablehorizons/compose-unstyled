package com.composeunstyled

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class StackTest {

    @Test
    fun horizontalOrientationPlacesItemsHorizontally() = runComposeUiTest {
        setContent {
            Stack(orientation = StackOrientation.Horizontal, modifier = Modifier.testTag("stack")) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("item1")
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("item2")
                )
            }
        }

        val item1Bounds = onNodeWithTag("item1").getBoundsInRoot()
        val item2Bounds = onNodeWithTag("item2").getBoundsInRoot()

        // Items should be placed horizontally (item2's left edge should be at or after item1's right edge)
        assert(item2Bounds.left >= item1Bounds.right) {
            "Items should be placed horizontally, but item2 (left=${item2Bounds.left}) is not to the right of item1 (right=${item1Bounds.right})"
        }
    }

    @Test
    fun verticalOrientationPlacesItemsVertically() = runComposeUiTest {
        setContent {
            Stack(orientation = StackOrientation.Vertical, modifier = Modifier.testTag("stack")) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("item1")
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("item2")
                )
            }
        }

        val item1Bounds = onNodeWithTag("item1").getBoundsInRoot()
        val item2Bounds = onNodeWithTag("item2").getBoundsInRoot()

        // Items should be placed vertically (item2's top edge should be at or below item1's bottom edge)
        assert(item2Bounds.top >= item1Bounds.bottom) {
            "Items should be placed vertically, but item2 (top=${item2Bounds.top}) is not below item1 (bottom=${item1Bounds.bottom})"
        }
    }

    @Test
    fun changingOrientationChangesPlacement() = runComposeUiTest {
        var orientation by mutableStateOf(StackOrientation.Horizontal)
        
        setContent {
            Stack(orientation = orientation, modifier = Modifier.testTag("stack")) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("item1")
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("item2")
                )
            }
        }

        // Initial horizontal placement
        val horizontalItem1Bounds = onNodeWithTag("item1").getBoundsInRoot()
        val horizontalItem2Bounds = onNodeWithTag("item2").getBoundsInRoot()
        assert(horizontalItem2Bounds.left >= horizontalItem1Bounds.right)

        // Change to vertical
        orientation = StackOrientation.Vertical

        // Verify vertical placement
        val verticalItem1Bounds = onNodeWithTag("item1").getBoundsInRoot()
        val verticalItem2Bounds = onNodeWithTag("item2").getBoundsInRoot()
        assert(verticalItem2Bounds.top >= verticalItem1Bounds.bottom)
    }
}