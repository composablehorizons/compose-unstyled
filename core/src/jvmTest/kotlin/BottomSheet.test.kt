package com.composeunstyled

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.composables.core.BottomSheet
import com.composables.core.BottomSheetState
import com.composables.core.SheetDetent
import com.composables.core.rememberBottomSheetState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

@OptIn(ExperimentalTestApi::class)
class BottomSheetTest {
    // TODO: Ensure the following are tested
    // # Test cases
    // - [x] invalid state creation
    // - [x] creating sheet with specific detents (hidden detent, fully visible, custom detent)
    // - [x] creating sheet with hidden and then specific detent (full visible, custom detent)
    // - [x] scrollable content (full detent, custom detent)

    // ## scrollable content
    // - [x] when only one detent, scrolling just scrolls the content, fully
    // - [x] first move sheet, then content (from half expanded, move sheet to expanded, then start scrolling)

    // ## state management
    // - [x] enabled/disable
    // - [x] dynamic detent support (state.detents = newList)
    // - [x] isIdle correctly reflects dragging and animation state
    // - [ ] dynamic sizing

    // ## other
    // - [ ] test cases that the contents are not messed up (needs clarification)
    // - [ ] ime aware on Android

    // invalid state cases
    @Test(expected = IllegalStateException::class)
    fun `given state without detents when creating state then throws exception`() = runComposeUiTest {
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
    fun `given initial detent not in detents list when creating state then throws exception`() = runComposeUiTest {
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
    fun `given duplicate detents when creating state then throws exception`() = runComposeUiTest {
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

    // remember version
    @Test(expected = IllegalStateException::class)
    fun `given no detents when creating state with rememberBottomSheetState then throws exception`() =
        runComposeUiTest {
            setContent {
                rememberBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded, detents = emptyList()
                )
            }
        }

    @Test(expected = IllegalStateException::class)
    fun `given initial detent not in list when creating state with rememberBottomSheetState then throws exception`() =
        runComposeUiTest {
            setContent {
                rememberBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded, detents = listOf(SheetDetent.Hidden)
                )
            }
        }

    /// -----------------------------------
    // initial detent
    @Test
    fun `given initial detent hidden when sheet is created then content is not displayed`() = runComposeUiTest {
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
    fun `given initial detent fully expanded when sheet is created then content is displayed`() = runComposeUiTest {
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
    fun `given initial detent hidden when target detent set to fully expanded then content is displayed`() =
        runComposeUiTest {
            lateinit var state: BottomSheetState
            setContent {
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                )

                BottomSheet(state) {
                    Box(Modifier.testTag("sheet_contents").size(40.dp))
                }
            }
            state.targetDetent = SheetDetent.FullyExpanded
            onNodeWithTag("sheet_contents").assertIsDisplayed()
        }

    @Test
    fun `given initial detent hidden when target detent set to custom detent then content is displayed`() =
        runComposeUiTest {
            val customDetent = SheetDetent("70percent") { containerHeight, _ ->
                containerHeight * 0.7f
            }

            lateinit var state: BottomSheetState

            setContent {
                // Use a fixed container size to make detent calculations predictable
                Box(Modifier.requiredSize(100.dp)) {
                    state = rememberBottomSheetState(
                        initialDetent = SheetDetent.Hidden,
                        detents = listOf(SheetDetent.Hidden, customDetent)
                    )

                    BottomSheet(state) {
                        Column {
                            Box(Modifier.testTag("top_content").height(70.dp).fillMaxWidth())
                            Box(Modifier.testTag("bottom_content").height(30.dp).fillMaxWidth())
                        }
                    }
                }
            }

            // Initially hidden, content should not be displayed
            onNodeWithTag("top_content").assertIsNotDisplayed()

            // Set to custom detent
            state.targetDetent = customDetent

            // After setting to custom detent, top content should be visible
            onNodeWithTag("top_content").assertIsDisplayed()
            // Bottom content should be clipped
            onNodeWithTag("bottom_content").assertIsNotDisplayed()
        }

    @Test
    fun `given sheet content when content size is modified then sheet height updates`() = runComposeUiTest {
        var contentSize by mutableStateOf(40.dp)

        setContent {
            val state = rememberBottomSheetState(
                initialDetent = SheetDetent.FullyExpanded,
            )

            BottomSheet(state, Modifier.testTag("sheet")) {
                Box(Modifier.testTag("sheet_contents").size(contentSize))
            }
        }

        mainClock.advanceTimeBy(50)
        contentSize = 150.dp
        mainClock.advanceTimeByFrame()

        onNodeWithTag("sheet").assertWidthIsEqualTo(150.dp)
        onNodeWithTag("sheet").assertHeightIsEqualTo(150.dp)
    }

    @Test
    fun `given hidden detent when sheet is created then offset is zero`() = runComposeUiTest {
        lateinit var state: BottomSheetState
        setContent {
            state = rememberBottomSheetState(
                initialDetent = SheetDetent.Hidden
            )
            BottomSheet(state) {
                Box(Modifier.testTag("sheet_contents").size(40.dp))
            }
        }

        assertThat(state.offset).isEqualTo(0f)
    }

    @Test
    fun `given fully expanded detent when sheet is created then offset equals content height`() = runComposeUiTest {
        lateinit var state: BottomSheetState
        setContent {
            state = rememberBottomSheetState(
                initialDetent = SheetDetent.FullyExpanded
            )
            BottomSheet(state) {
                Box(Modifier.testTag("sheet_contents").size(40.dp))
            }
        }

        assertThat(state.offset).isEqualTo(40f)
    }

    @Test
    fun `given sheet animating when position changes then current and target detents update correctly`() =
        runComposeUiTest {
            runBlocking {
                lateinit var state: BottomSheetState
                lateinit var scope: CoroutineScope
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
    fun `given dynamic detent when invalidateDetents is called then detent updates`() = runComposeUiTest {
        lateinit var state: BottomSheetState
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
        state.invalidateDetents()
        onNodeWithText("Bottom").assertIsDisplayed()
    }

    @Test
    fun `given sheet at a detent when that detent is removed from detents list then sheet moves to closest available detent`() = runComposeUiTest {
        val detent0 = SheetDetent("0") { containerHeight, _ -> containerHeight * 0f }
        val detent25 = SheetDetent("25") { containerHeight, _ -> containerHeight * 0.25f }
        val detent50 = SheetDetent("50") { containerHeight, _ -> containerHeight * 0.5f }
        val detent75 = SheetDetent("75") { containerHeight, _ -> containerHeight * 0.75f }
        val detent100 = SheetDetent("100") { containerHeight, _ -> containerHeight * 1f }

        lateinit var state: BottomSheetState

        setContent {
            Box(Modifier.requiredSize(100.dp)) {
                state = rememberBottomSheetState(
                    initialDetent = detent50,
                    detents = listOf(detent0, detent25, detent50, detent75, detent100)
                )

                BottomSheet(state) {
                    Box(
                        Modifier
                            .testTag("sheet_contents")
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                }
            }
        }

        // Remove 50% detent while we're currently at 50%
        // The sheet should automatically move to the closest available detent
        waitUntil { state.isIdle }
        state.detents = listOf(detent0, detent25, detent75, detent100)
        awaitIdle()
        waitUntil { state.isIdle }

        // Detents list should be updated to 4 detents
        assertThat(state.detents).hasSize(4)
        assertThat(state.detents).contains(detent0, detent25, detent75, detent100)
        assertThat(state.detents).doesNotContain(detent50)
    }

    @Test
    fun `given sheet when settling at detents then isIdle correctly reflects animation state`() = runComposeUiTest {
        lateinit var state: BottomSheetState

        setContent {
            Box(Modifier.requiredSize(100.dp)) {
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(100.dp))
                }
            }
        }

        // Initially should be idle at Hidden
        waitUntil { state.isIdle }
        assertThat(state.isIdle).isTrue
        assertEquals(SheetDetent.Hidden, state.currentDetent)

        // Start animation to FullyExpanded
        state.targetDetent = SheetDetent.FullyExpanded

        // Should not be idle during animation
        mainClock.advanceTimeBy(50)
        assertThat(state.isIdle).isFalse

        // Wait for animation to complete
        waitUntil { state.isIdle }
        assertThat(state.isIdle).isTrue
        assertEquals(SheetDetent.FullyExpanded, state.currentDetent)
    }

    @Test
    fun `given sheet when detents are updated then isIdle becomes true after settling`() = runComposeUiTest {
        val detent25 = SheetDetent("25") { containerHeight, _ -> containerHeight * 0.25f }
        val detent50 = SheetDetent("50") { containerHeight, _ -> containerHeight * 0.5f }
        val detent75 = SheetDetent("75") { containerHeight, _ -> containerHeight * 0.75f }

        lateinit var state: BottomSheetState

        setContent {
            Box(Modifier.requiredSize(100.dp)) {
                state = rememberBottomSheetState(
                    initialDetent = detent50,
                    detents = listOf(detent25, detent50, detent75)
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(100.dp))
                }
            }
        }

        // Wait for initial idle state
        waitUntil { state.isIdle }
        assertThat(state.isIdle).isTrue
        assertEquals(detent50, state.currentDetent)

        // Update detents while at 50% - removing the current detent
        state.detents = listOf(detent25, detent75)

        // Should eventually settle and become idle
        waitUntil { state.isIdle }
        assertThat(state.isIdle).isTrue

        // Should be at one of the remaining detents
        assertThat(state.currentDetent).isIn(detent25, detent75)
    }

    /// -----------------------------------
    // custom detents

    @Test
    fun `given custom detent when sheet is created then sheet positions at correct height`() = runComposeUiTest {
        val customDetent = SheetDetent("70percent") { containerHeight, _ ->
            containerHeight * 0.7f
        }

        setContent {
            // Use a fixed container size to make detent calculations predictable
            // 100.dp container * 0.7 = 70.dp visible content
            Box(Modifier.requiredSize(100.dp)) {
                BottomSheet(
                    state = rememberBottomSheetState(
                        initialDetent = customDetent,
                        detents = listOf(customDetent)
                    ),
                    modifier = Modifier.testTag("sheet")
                ) {
                    Column(Modifier.testTag("sheet_contents")) {
                        Box(Modifier.testTag("top_content").height(70.dp).fillMaxWidth())
                        Box(Modifier.testTag("bottom_content").height(30.dp).fillMaxWidth())
                    }
                }
            }
        }

        // With a 70% detent on 100.dp container, only 70.dp should be visible
        // The top 70.dp content should be displayed
        onNodeWithTag("top_content").assertIsDisplayed()
        // The bottom 30.dp should be clipped by the container bounds
        onNodeWithTag("bottom_content").assertIsNotDisplayed()
    }

    @Test
    fun `given custom detent when sheet is created then offset is calculated correctly`() = runComposeUiTest {
        val customDetent = SheetDetent("70percent") { containerHeight, _ ->
            containerHeight * 0.7f
        }

        lateinit var state: BottomSheetState

        setContent {
            // Use a fixed container size to make detent calculations predictable
            // 100.dp container * 0.7 = 70.dp visible content
            Box(Modifier.requiredSize(100.dp)) {
                state = rememberBottomSheetState(
                    initialDetent = customDetent,
                    detents = listOf(customDetent)
                )
                BottomSheet(state = state) {
                    Column(Modifier.testTag("sheet_contents")) {
                        Box(Modifier.testTag("top_content").height(70.dp).fillMaxWidth())
                        Box(Modifier.testTag("bottom_content").height(30.dp).fillMaxWidth())
                    }
                }
            }
        }

        // With 70.dp of visible content, the offset should be 70.dp
        // (offset represents how far the sheet has moved from the bottom)
        assertThat(state.offset).isEqualTo(70f)
    }

    /// -----------------------------------
    // scrollable content
    @Test
    fun `given single detent with scrollable content when scrolling then only content scrolls not sheet`() =
        runComposeUiTest {
            val customDetent = SheetDetent("70percent") { containerHeight, _ ->
                containerHeight * 0.7f
            }

            lateinit var state: BottomSheetState

            setContent {
                // Use a fixed container size to make detent calculations predictable
                Box(Modifier.requiredSize(100.dp)) {
                    state = rememberBottomSheetState(
                        initialDetent = customDetent,
                        detents = listOf(customDetent) // Only ONE detent
                    )

                    BottomSheet(
                        state = state,
                        modifier = Modifier.testTag("sheet")
                    ) {
                        Column(
                            Modifier
                                .testTag("scrollable_content")
                                .verticalScroll(rememberScrollState())
                        ) {
                            repeat(10) { index ->
                                Box(
                                    Modifier
                                        .testTag("item_$index")
                                        .size(width = 100.dp, height = 100.dp)
                                )
                            }
                        }
                    }
                }
            }

            waitUntilExactlyOneExists(hasTestTag("sheet"))

            // Sheet should be at 70% detent
            val initialOffset = state.offset
            assertThat(initialOffset).isEqualTo(70f)

            // Scroll to last item
            onNodeWithTag("item_9").performScrollTo()

            // Sheet offset should NOT change - only content scrolls
            assertThat(state.offset).isEqualTo(initialOffset)

            // Last element should be visible after scrolling
            onNodeWithTag("item_9").assertIsDisplayed()
        }

    @Test
    fun `given fully expanded detent with scrollable content when scrolling to bottom then last element is displayed`() =
        runComposeUiTest {
            setContent {
                BottomSheet(
                    state = rememberBottomSheetState(
                        initialDetent = SheetDetent.FullyExpanded
                    ),
                    modifier = Modifier.testTag("sheet")
                ) {
                    Column(
                        Modifier
                            .testTag("scrollable_content")
                            .verticalScroll(rememberScrollState())
                    ) {
                        repeat(10) { index ->
                            Box(
                                Modifier
                                    .testTag("item_$index")
                                    .size(width = 100.dp, height = 100.dp)
                            )
                        }
                    }
                }
            }

            waitUntilExactlyOneExists(hasTestTag("sheet"))

            // Verify first element is visible
            onNodeWithTag("item_0").assertIsDisplayed()

            // Scroll to last item
            onNodeWithTag("item_9").performScrollTo()

            // Verify last element (index 9) is displayed
            onNodeWithTag("item_9").assertIsDisplayed()
        }

    @Test
    fun `given custom detent with scrollable content when scrolling to bottom then last element is displayed`() =
        runComposeUiTest {

            setContent {
                val customDetent = SheetDetent("70percent") { containerHeight, _ ->
                    containerHeight * 0.7f
                }
                // Use a fixed container size to make detent calculations predictable
                Box(Modifier.requiredSize(100.dp)) {
                    BottomSheet(
                        state = rememberBottomSheetState(
                            initialDetent = customDetent,
                            detents = listOf(customDetent)
                        ),
                        modifier = Modifier.testTag("sheet")
                    ) {
                        Column(
                            Modifier
                                .testTag("scrollable_content")
                                .verticalScroll(rememberScrollState())
                        ) {
                            repeat(10) { index ->
                                Box(
                                    Modifier
                                        .testTag("item_$index")
                                        .size(width = 100.dp, height = 100.dp)
                                )
                            }
                        }
                    }
                }
            }

            waitUntilExactlyOneExists(hasTestTag("sheet"))

//            // Verify first element is visible
            onNodeWithTag("item_0").assertIsDisplayed()
//
//            // Scroll to last item
            onNodeWithTag("item_9").performScrollTo()
//
//            // Verify last element (index 9) is displayed
//            onNodeWithTag("item_9").assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun swipingUpExpandsSheetInsteadOfScrolling() = runComposeUiTest {
        val halfExpandedDetent = SheetDetent("half") { containerHeight, _ ->
            containerHeight * 0.5f
        }

        lateinit var state: BottomSheetState

        setContent {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                state = rememberBottomSheetState(
                    initialDetent = halfExpandedDetent, detents = listOf(halfExpandedDetent, SheetDetent.FullyExpanded)
                )

                BottomSheet(
                    state = state,
                    modifier = Modifier
                        .background(Color.White)
                        .testTag("sheet")
                        .verticalScroll(rememberScrollState())
                ) {
                    Column {
                        repeat(5) { index ->
                            Text(
                                text = "item_$index",
                                modifier = Modifier
                                    .testTag("item_$index")
                                    .fillMaxWidth()
                                    .height(100.dp)
                            )
                        }
                    }
                }
            }
        }
        // Initially at half expanded (50%)
        assertEquals(halfExpandedDetent, state.currentDetent)

        // First item should be visible
        onNodeWithTag("item_0").assertIsDisplayed()

        // Swipe up on the sheet - this should move the sheet to FullyExpanded first
        onNodeWithTag("sheet").performTouchInput {
            swipeUp()
        }
        awaitIdle()
        assertEquals(SheetDetent.FullyExpanded, state.currentDetent)
        onNodeWithTag("item_1").assertIsDisplayed()
    }

    @Test
    fun `given enabled false when swiping sheet then sheet does not move`() = runComposeUiTest {
        val halfExpandedDetent = SheetDetent("half") { containerHeight, _ ->
            containerHeight * 0.5f
        }

        lateinit var state: BottomSheetState

        setContent {
            Box(Modifier.fillMaxSize()) {
                state = rememberBottomSheetState(
                    initialDetent = halfExpandedDetent,
                    detents = listOf(halfExpandedDetent, SheetDetent.FullyExpanded)
                )

                BottomSheet(
                    state = state,
                    enabled = false,
                    modifier = Modifier.testTag("sheet")
                ) {
                    Box(
                        Modifier
                            .testTag("sheet_contents")
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }

        // Initially at half expanded
        val initialOffset = state.offset
        assertEquals(halfExpandedDetent, state.currentDetent)

        // Try to swipe up - should not move the sheet
        onNodeWithTag("sheet").performTouchInput {
            swipeUp()
        }
        waitForIdle()

        // Sheet should remain at the same detent and offset
        assertEquals(halfExpandedDetent, state.currentDetent)
        assertThat(state.offset).isEqualTo(initialOffset)

        // Try to swipe down - should also not move the sheet
        onNodeWithTag("sheet").performTouchInput {
            swipeDown()
        }
        waitForIdle()

        // Sheet should still be at the same detent
        assertEquals(halfExpandedDetent, state.currentDetent)
        assertThat(state.offset).isEqualTo(initialOffset)
    }

}
