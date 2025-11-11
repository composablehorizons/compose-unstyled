package com.composeunstyled

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertNotEquals

@OptIn(ExperimentalTestApi::class)
class FloatingContentTest {

    @Test
    fun rendersNothingWhenNoAnchor() = runComposeUiTest {
        // FloatingContent should not render floating content when there's no anchor
        setContent {
            FloatingContent(
                floatingContent = {
                    Text("Floating content")
                },
                anchor = {}
            )
        }

        waitForIdle()
        onNodeWithText("Floating content").assertDoesNotExist()
    }

    @Test
    fun rendersOnlyAnchorWhenNoFloatingContent() = runComposeUiTest {
        // FloatingContent should render only the anchor when there's no floating content
        setContent {
            FloatingContent(
                floatingContent = {},
                anchor = {
                    Text("Anchor")
                }
            )
        }

        waitForIdle()
        onNodeWithText("Anchor").assertExists()
    }

    @Test
    fun placementChangesFloatingContentPosition() = runComposeUiTest {
        // Test that different placements result in different positions
        var topStartY = 0f
        var bottomStartY = 0f

        // First, test with TopStart placement
        setContent {
            FloatingContent(
                placement = RelativeAlignment.TopStart,
                floatingContent = {
                    Box(Modifier.size(50.dp)) {
                        Text("Floating")
                    }
                },
                anchor = {
                    Box(Modifier.size(100.dp)) {
                        Text("Anchor")
                    }
                }
            )
        }

        waitForIdle()
        topStartY = onNodeWithText("Floating").fetchSemanticsNode().positionInRoot.y

        // Now test with BottomStart placement
        setContent {
            FloatingContent(
                placement = RelativeAlignment.BottomStart,
                floatingContent = {
                    Box(Modifier.size(50.dp)) {
                        Text("Floating")
                    }
                },
                anchor = {
                    Box(Modifier.size(100.dp)) {
                        Text("Anchor")
                    }
                }
            )
        }

        waitForIdle()
        bottomStartY = onNodeWithText("Floating").fetchSemanticsNode().positionInRoot.y

        // The Y positions should be different when placement changes
        assertNotEquals(topStartY, bottomStartY, "Floating content position should change when placement changes")
    }

    @Test
    fun floatingContentClampsToWindowBounds() = runComposeUiTest {
        // FloatingContent clamps to window bounds like a Popup
        setContent {
            FloatingContent(
                placement = RelativeAlignment.TopStart,
                floatingContent = {
                    Box(Modifier.size(100.dp)) {
                        Text("Floating")
                    }
                },
                anchor = {
                    Box(Modifier.size(50.dp)) {
                        Text("Anchor")
                    }
                }
            )
        }

        waitForIdle()

        val floatingY = onNodeWithText("Floating").fetchSemanticsNode().positionInRoot.y

        // With TopStart placement at the top of the window,
        // floating content should be clamped to not go negative
        assert(floatingY >= 0f) {
            "Floating content should be clamped to window bounds (floatingY=$floatingY should be >= 0)"
        }
    }

    @Test
    fun floatingContentLargerThanWindow() = runComposeUiTest {
        // When floating content is larger than the window,
        // it should be clamped to start at 0 to maximize visible area
        setContent {
            FloatingContent(
                placement = RelativeAlignment.BottomStart,
                floatingContent = {
                    // Make floating content very large (larger than typical window)
                    Box(Modifier.size(10000.dp)) {
                        Text("Large floating")
                    }
                },
                anchor = {
                    Box(Modifier.size(50.dp)) {
                        Text("Anchor")
                    }
                }
            )
        }

        waitForIdle()

        val floatingNode = onNodeWithText("Large floating").fetchSemanticsNode()
        val floatingX = floatingNode.positionInRoot.x
        val floatingY = floatingNode.positionInRoot.y

        // When content is larger than window, it should be clamped to 0
        // to maximize the visible portion at the top-left
        assert(floatingX >= 0f) {
            "Large floating content should be clamped to x >= 0 (got x=$floatingX)"
        }
        assert(floatingY >= 0f) {
            "Large floating content should be clamped to y >= 0 (got y=$floatingY)"
        }
    }
}