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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.composables.core.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat

@OptIn(ExperimentalTestApi::class)
class BottomSheetTest {

    @Test
    fun `ensure valid state created`() = runTestSuite {
        testCase(
            name = "throws exception, when creating state without detents",
            expected = IllegalStateException::class,
        ) {
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

        testCase(
            name = "throws exception, when initial detent not in detents list",
            expected = IllegalStateException::class
        ) {
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

        testCase(
            name = "throws exception, when creating state with duplicate detents",
            expected = IllegalStateException::class
        ) {
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

        testCase(
            name = "throws exception, when creating state with rememberBottomSheetState without detents",
            expected = IllegalStateException::class
        ) {
            setContent {
                rememberBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded, detents = emptyList()
                )
            }
        }

        testCase(
            name = "throws exception, when creating state with rememberBottomSheetState with initial detent not in list",
            expected = IllegalStateException::class
        ) {
            setContent {
                rememberBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded, detents = listOf(SheetDetent.Hidden)
                )
            }
        }
    }

    @Test
    fun `state properties`() = runTestSuite {
        testCase("setting state enabled to false, blocks dragging") {
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

            // Try to swipe up - should not move the sheet
            onNodeWithTag("sheet").performTouchInput {
                swipeUp()
            }
            waitForIdle()

            // Sheet should remain at the same detent and offset
            assertEquals(halfExpandedDetent, state.currentDetent)
            assertThat(state.offset).isEqualTo(initialOffset)
        }

        testCase("isIdle is false, when dragging sheet") {
            val halfDetent = SheetDetent("half") { _, _ ->
                100.dp
            }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = halfDetent,
                    detents = listOf(SheetDetent.Hidden, halfDetent, SheetDetent.FullyExpanded)
                )

                BottomSheet(state, Modifier.testTag("sheet")) {
                    Box(
                        Modifier
                            .testTag("sheet_contents")
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            }

            waitForIdle()
            assertThat(state.isIdle).isTrue

            mainClock.autoAdvance = false

            // Start dragging and move
            onNodeWithTag("sheet").performTouchInput {
                down(center)
                moveTo(center.copy(y = center.y - 50f))
            }
            mainClock.advanceTimeBy(50)

            assertThat(state.isIdle).isFalse
        }

        testCase("isIdle is false during animation, when sheet animates to new detent") {
            val halfDetent = SheetDetent("half") { _, _ ->
                100.dp
            }

            lateinit var state: BottomSheetState
            lateinit var scope: CoroutineScope

            setContent {
                scope = rememberCoroutineScope()
                state = rememberBottomSheetState(
                    initialDetent = halfDetent,
                    detents = listOf(SheetDetent.Hidden, halfDetent, SheetDetent.FullyExpanded),
                    animationSpec = tween(1000)
                )

                BottomSheet(state, Modifier.testTag("sheet")) {
                    Box(
                        Modifier
                            .testTag("sheet_contents")
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            }

            waitForIdle()
            mainClock.autoAdvance = false

            scope.launch {
                state.animateTo(SheetDetent.FullyExpanded)
            }
            mainClock.advanceTimeBy(50)

            assertThat(state.isIdle).isFalse

            mainClock.autoAdvance = true
            waitForIdle()

            assertThat(state.isIdle).isTrue
        }
    }

    @Test
    fun `initial state offset`() = runTestSuite {
        testCase("offset is 0, when sheet is created with hidden detent") {
            lateinit var state: BottomSheetState
            setContent {
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.Hidden
                )
                BottomSheet(state = state) {
                    Box(Modifier.testTag("sheet_contents").size(40.dp))
                }
            }

            onNodeWithTag("sheet_contents").assertIsNotDisplayed()
            assertThat(state.offset).isEqualTo(0f)
        }

        testCase("offset is height of content, when sheet is created with hidden detent") {
            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded,
                )
                BottomSheet(state) {
                    Box(Modifier.testTag("sheet_contents").size(40.dp))
                }
            }

            onNodeWithTag("sheet_contents").assertIsDisplayed()
            assertThat(state.offset).isEqualTo(40f)
        }

        testCase("offset is custom detent height, when creating sheet with custom initial detent") {
            val customDetent = SheetDetent("custom") { _, _ -> 50.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = customDetent,
                    detents = listOf(SheetDetent.Hidden, customDetent, SheetDetent.FullyExpanded)
                )

                BottomSheet(state) {
                    Box(Modifier.testTag("sheet_contents").size(60.dp))
                }
            }

            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(customDetent)
            assertThat(state.offset).isEqualTo(50f)

        }

        testCase("offset is zero, when sheet is created at hidden detent") {
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

        testCase("offset equals content height, when sheet is created at fully expanded detent") {
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
    }

    @Test
    fun `setting targetDetent`() = runTestSuite {
        testCase("sheet stops at height of content, when target detent set to fully expanded from hidden") {
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
            assertThat(state.offset).isEqualTo(40f)
        }

        testCase("sheet stops at detent fixed height, when target detent set to detent with fixed height from hidden") {
            val customDetent = SheetDetent("fixed") { _, _ -> 40.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    detents = listOf(SheetDetent.Hidden, customDetent)
                )

                BottomSheet(state) {
                    Box(Modifier.testTag("content").height(70.dp).fillMaxWidth())
                }
            }

            // Set to custom detent
            state.targetDetent = customDetent

            // Content should be displayed
            onNodeWithTag("content").assertIsDisplayed()
            assertThat(state.offset).isEqualTo(40f)

        }

        testCase("isIdle correctly reflects animation state, when sheet settles at detent") {
            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded)
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(100.dp))
                }
            }

            // Start animation to FullyExpanded
            state.targetDetent = SheetDetent.FullyExpanded

            // Should not be idle during animation
            mainClock.advanceTimeBy(50)
            assertThat(state.isIdle).isFalse
        }

        testCase("isIdle becomes true after settling, when detents are updated") {
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

            // Update detents while at 50% - removing the current detent
            state.detents = listOf(detent25, detent75)

            // Should eventually settle and become idle
            waitUntil { state.isIdle }
            assertThat(state.isIdle).isTrue
        }

        testCase("current and target detents update correctly, when dragging") {
            val halfDetent = SheetDetent("half") { _, _ ->
                150.dp
            }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = halfDetent,
                    detents = listOf(SheetDetent.Hidden, halfDetent, SheetDetent.FullyExpanded)
                )

                BottomSheet(state, Modifier.testTag("sheet")) {
                    Box(
                        Modifier
                            .testTag("sheet_contents")
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }
            }

            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(halfDetent)
            assertThat(state.targetDetent).isEqualTo(halfDetent)

            mainClock.autoAdvance = false

            // Start dragging upward
            onNodeWithTag("sheet").performTouchInput {
                down(center)
            }
            mainClock.advanceTimeBy(50)

            onNodeWithTag("sheet").performTouchInput {
                moveTo(center.copy(y = center.y - 150f))
            }
            mainClock.advanceTimeBy(50)

            // During drag, target should change to FullyExpanded, current stays at half
            assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)
            assertThat(state.currentDetent).isEqualTo(halfDetent)

            // Release
            onNodeWithTag("sheet").performTouchInput {
                up()
            }

            mainClock.autoAdvance = true
            waitForIdle()

            // After settling, both should be FullyExpanded
            assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
            assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)
        }
    }

    @Test
    fun `custom detents`() = runTestSuite {
        testCase("sheet matches container size, when using container-based detent") {
            val containerDetent = SheetDetent("container") { containerHeight, _ ->
                containerHeight
            }

            lateinit var state: BottomSheetState

            setContent {
                Box(Modifier.requiredSize(300.dp)) {
                    state = rememberBottomSheetState(
                        initialDetent = containerDetent,
                        detents = listOf(containerDetent)
                    )

                    BottomSheet(state, Modifier.testTag("sheet")) {
                        Box(
                            Modifier
                                .testTag("sheet_contents")
                                .fillMaxSize()
                        )
                    }
                }
            }

            waitForIdle()

            assertThat(state.offset).isEqualTo(300f)
            onNodeWithTag("sheet").assertHeightIsEqualTo(300.dp)
        }
        testCase("sheet matches sheet content height, when using content-sized detent") {
            val contentDetent = SheetDetent("content") { _, sheetHeight ->
                sheetHeight
            }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = contentDetent,
                    detents = listOf(contentDetent)
                )

                BottomSheet(state, Modifier.testTag("sheet")) {
                    Box(
                        Modifier
                            .testTag("sheet_contents")
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

            waitForIdle()

            assertThat(state.offset).isEqualTo(200f)
            onNodeWithTag("sheet").assertHeightIsEqualTo(200.dp)
        }
        testCase("sheet uses fixed height, when using fixed-height detent") {
            // Fixed height detent - always returns 150.dp regardless of container or content
            val fixedDetent = SheetDetent("fixed") { _, _ ->
                150.dp
            }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = fixedDetent,
                    detents = listOf(fixedDetent)
                )

                BottomSheet(state, Modifier.testTag("sheet")) {
                    Column(Modifier.testTag("sheet_contents")) {
                        Box(Modifier.testTag("visible_content").height(150.dp).fillMaxWidth())
                        Box(Modifier.testTag("hidden_content").height(100.dp).fillMaxWidth())
                    }
                }
            }

            waitForIdle()

            // Sheet should be at exactly 150.dp
            assertThat(state.offset).isEqualTo(150f)
        }
        testCase("sheet takes full container size, when using content-sized detent and content is fillMaxSize()") {
            val contentDetent = SheetDetent("content") { _, sheetHeight ->
                sheetHeight
            }

            lateinit var state: BottomSheetState

            setContent {
                Box(Modifier.requiredSize(300.dp)) {
                    state = rememberBottomSheetState(
                        initialDetent = contentDetent,
                        detents = listOf(contentDetent)
                    )

                    BottomSheet(state, Modifier.testTag("sheet")) {
                        Box(
                            Modifier
                                .testTag("sheet_contents")
                                .fillMaxSize()
                        )
                    }
                }
            }

            waitForIdle()

            assertThat(state.offset).isEqualTo(300f)
            onNodeWithTag("sheet").assertHeightIsEqualTo(300.dp)
        }
    }

    @Test
    fun `dynamic content sizing`() = runTestSuite {
        testCase("offset matches sheet content height, when sheet contents grow") {
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

            onNodeWithTag("sheet").assertHeightIsEqualTo(150.dp)
        }

        testCase("offset matches sheet content height, when sheet contents shrink") {
            var contentHeight by mutableStateOf(200.dp)
            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded,
                )

                BottomSheet(state, Modifier.testTag("sheet")) {
                    Box(
                        Modifier
                            .testTag("sheet_contents")
                            .fillMaxWidth()
                            .height(contentHeight)
                    )
                }
            }

            // Shrink content
            contentHeight = 100.dp

            onNodeWithTag("sheet").assertHeightIsEqualTo(100.dp)
        }

        testCase("offset matches detent fixed height, when sheet contents changes") {
            // Fixed height detent - always 100.dp
            val fixedDetent = SheetDetent("fixed100") { _, _ ->
                100.dp
            }

            var containerSize by mutableStateOf(200.dp)
            lateinit var state: BottomSheetState

            setContent {
                Box(Modifier.requiredSize(containerSize)) {
                    state = rememberBottomSheetState(
                        initialDetent = fixedDetent,
                        detents = listOf(fixedDetent)
                    )

                    BottomSheet(state) {
                        Box(
                            Modifier
                                .testTag("sheet_contents")
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                    }
                }
            }

            // Change container size to 400.dp
            containerSize = 400.dp
            state.invalidateDetents()
            mainClock.advanceTimeByFrame()
            waitForIdle()

            // Even though container changed from 200.dp to 400.dp,
            // fixed detent should still be at 100.dp (not affected by container size)
            assertThat(state.offset).isEqualTo(100f)

        }

        testCase("sheet takes full container size, when using content-sized detent and content is fillMaxSize") {
            // Content-sized detent that uses the sheet's content height
            val contentDetent = SheetDetent("content") { _, sheetHeight ->
                sheetHeight
            }

            lateinit var state: BottomSheetState

            setContent {
                Box(Modifier.requiredSize(400.dp)) {
                    state = rememberBottomSheetState(
                        initialDetent = contentDetent,
                        detents = listOf(contentDetent)
                    )

                    BottomSheet(state, Modifier.testTag("sheet")) {
                        Box(
                            Modifier
                                .testTag("sheet_contents")
                                .fillMaxSize()
                                .background(Color.White)
                        )
                    }
                }
            }

            waitForIdle()

            // When content uses fillMaxSize(), it should match the container height (400.dp)
            assertThat(state.offset).isEqualTo(400f)
            onNodeWithTag("sheet").assertHeightIsEqualTo(400.dp)
        }
    }

    @Test
    fun animateTo() = runTestSuite {
        testCase("current and target detents update correctly, when sheet animates to new position") {
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

            // sheet starting moving towards at FullyExpanded
            scope.launch {
                state.animateTo(SheetDetent.FullyExpanded)
            }
            mainClock.advanceTimeBy(1000L)

            // During animation, target should be FullyExpanded
            assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)
        }
    }

    @Test
    fun invalidateDetents() = runTestSuite {
        testCase("detent updates, when invalidateDetents is called on dynamic detent") {
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
            detentHeight += 50.dp
            state.invalidateDetents()
            onNodeWithText("Bottom").assertIsDisplayed()
        }

        testCase("target detent stays the same, when invalidating detents mid-drag") {
            val halfDetent = SheetDetent("half") { containerHeight, _ ->
                containerHeight * 0.5f
            }

            lateinit var state: BottomSheetState

            setContent {
                Box(Modifier.requiredSize(400.dp)) {
                    state = rememberBottomSheetState(
                        initialDetent = halfDetent,
                        detents = listOf(SheetDetent.Hidden, halfDetent, SheetDetent.FullyExpanded)
                    )

                    BottomSheet(state, Modifier.testTag("sheet")) {
                        Box(
                            Modifier
                                .testTag("sheet_contents")
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                    }
                }
            }

            mainClock.autoAdvance = false

            // Start manual drag gesture
            onNodeWithTag("sheet").performTouchInput {
                down(bottomCenter)
            }

            mainClock.advanceTimeBy(50)

            // Move up to trigger upward gesture
            onNodeWithTag("sheet").performTouchInput {
                moveTo(center.copy(y = center.y - 300f))
            }

            mainClock.advanceTimeBy(100)

            // Capture target detent before invalidation
            val originalDetent = state.targetDetent

            // Invalidate detents mid-drag
            state.invalidateDetents()
            mainClock.advanceTimeByFrame()

            // Target detent should remain the same after invalidation
            assertThat(state.targetDetent).isEqualTo(originalDetent)
        }

        testCase("sheet moves with content, when content changes at content-based detent") {
            var contentHeight by mutableStateOf(100.dp)

            // Content-based detent that returns the content height
            val contentDetent = SheetDetent("content") { _, sheetHeight ->
                sheetHeight
            }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = contentDetent,
                    detents = listOf(contentDetent)
                )

                BottomSheet(state) {
                    Box(
                        Modifier
                            .testTag("sheet_contents")
                            .fillMaxWidth()
                            .height(contentHeight)
                    )
                }
            }

            // Grow content
            contentHeight = 200.dp
            state.invalidateDetents()
            mainClock.advanceTimeByFrame()
            waitForIdle()

            // Offset should move with content to new height
            assertThat(state.offset).isEqualTo(200f)
        }
    }

    @Test
    fun `updating detents`() = runTestSuite {
        testCase("state.detents updates, when setting new detents list") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }
            val detent300 = SheetDetent("300") { _, _ -> 300.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200)
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            waitForIdle()
            assertThat(state.detents).isEqualTo(listOf(detent100, detent200))

            state.detents = listOf(detent100, detent200, detent300)
            waitForIdle()

            assertThat(state.detents).isEqualTo(listOf(detent100, detent200, detent300))
        }

        testCase("sheet moves to closest detent upward, when current detent removed while moving up") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }
            val detent300 = SheetDetent("300") { _, _ -> 300.dp }
            val detent400 = SheetDetent("400") { _, _ -> 400.dp }

            lateinit var state: BottomSheetState
            lateinit var scope: CoroutineScope

            setContent {
                scope = rememberCoroutineScope()
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200, detent300, detent400),
                    animationSpec = tween(2000)
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            // Start moving up toward detent200
            scope.launch {
                state.animateTo(detent200)
            }
            // Verify we're actually moving toward detent200
            assertThat(state.targetDetent).isEqualTo(detent200)
            assertThat(state.isIdle).isFalse

            // Remove detent200 while animating toward it
            state.detents = listOf(detent100, detent300, detent400)

            // Should move to detent300 (closest upward)
            assertThat(state.targetDetent).isEqualTo(detent300)
            awaitIdle()
            assertThat(state.currentDetent).isEqualTo(detent300)
        }

        testCase(
            "sheet moves to closest detent downward, when current detent removed while moving down",
        ) {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }
            val detent300 = SheetDetent("300") { _, _ -> 300.dp }
            val detent400 = SheetDetent("400") { _, _ -> 400.dp }

            lateinit var state: BottomSheetState
            lateinit var scope: CoroutineScope

            setContent {
                scope = rememberCoroutineScope()
                state = rememberBottomSheetState(
                    initialDetent = detent400,
                    detents = listOf(detent100, detent200, detent300, detent400),
                    animationSpec = tween(2000)
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(500.dp))
                }
            }

            waitUntil { state.isIdle }
            mainClock.autoAdvance = false

            // Start moving down toward detent300
            scope.launch {
                state.animateTo(detent300)
            }
            mainClock.advanceTimeBy(100)

            // Verify we're actually moving toward detent300
            assertThat(state.targetDetent).isEqualTo(detent300)
            assertThat(state.isIdle).isFalse

            // Remove detent300 while animating toward it
            state.detents = listOf(detent100, detent200, detent400)

            mainClock.autoAdvance = true
            waitUntil { state.isIdle }

            // Should move to detent200 (closest downward)
            assertThat(state.currentDetent).isEqualTo(detent200)
        }

        testCase(
            name = "throws exception, when setting detents to empty list",
            expected = IllegalStateException::class
        ) {
            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded,
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            state.detents = emptyList()
        }

        testCase("sheet moves to first detent, when no detent found in search direction") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }
            val detent300 = SheetDetent("300") { _, _ -> 300.dp }
            val detent400 = SheetDetent("400") { _, _ -> 400.dp }
            val detent500 = SheetDetent("500") { _, _ -> 500.dp }

            lateinit var state: BottomSheetState
            lateinit var scope: CoroutineScope

            setContent {
                scope = rememberCoroutineScope()
                state = rememberBottomSheetState(
                    initialDetent = detent400,
                    detents = listOf(detent100, detent200, detent300, detent400, detent500),
                    animationSpec = tween(2000)
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(500.dp))
                }
            }

            waitUntil { state.isIdle }
            mainClock.autoAdvance = false

            // Start moving down toward detent300
            scope.launch {
                state.animateTo(detent300)
            }
            mainClock.advanceTimeBy(100)

            // Verify we're actually moving toward detent300
            assertThat(state.targetDetent).isEqualTo(detent300)
            assertThat(state.isIdle).isFalse

            // Remove all detents below detent400, leaving only detent400 and detent500
            // No valid detent in downward direction - should fall back to first detent
            state.detents = listOf(detent400, detent500)

            mainClock.autoAdvance = true
            waitUntil { state.isIdle }

            // Should move to first detent (detent400)
            assertThat(state.currentDetent).isEqualTo(detent400)
        }
    }

    @Test
    fun `accessibility semantics`() = runTestSuite {
        testCase("drag indication has expand action, when sheet can be expanded") {
            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(300.dp))
                }
            }

            waitForIdle()

            onNodeWithTag("drag_indication")
                .assert(hasExpandAction())
        }

        testCase("drag indication has collapse action, when sheet is expanded") {
            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded,
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(300.dp))
                }
            }

            waitForIdle()

            onNodeWithTag("drag_indication")
                .assert(hasCollapseAction())
        }

        testCase("drag indication has no expand action, when sheet is at topmost detent") {
            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded,
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(300.dp))
                }
            }

            waitForIdle()

            onNodeWithTag("drag_indication")
                .assert(hasNoExpandAction())
        }

        testCase("drag indication has no collapse action, when sheet is at bottommost detent") {
            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = SheetDetent.Hidden,
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(300.dp))
                }
            }

            waitForIdle()

            onNodeWithTag("drag_indication")
                .assert(hasNoCollapseAction())
        }
    }

    @Test
    fun `scrollable content`() = runTestSuite {
        testCase("scrolls content, when only one detent") {
            val customDetent = SheetDetent("custom") { _, _ -> 70.dp }
            lateinit var state: BottomSheetState

            setContent {
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

            waitUntilExactlyOneExists(hasTestTag("sheet"))

            // Sheet should be at fixed detent
            val initialOffset = state.offset

            // Scroll to last item
            onNodeWithTag("item_9").performScrollTo()

            // Sheet offset should NOT change - only content scrolls
            assertThat(state.offset).isEqualTo(initialOffset)

        }

        testCase("expands sheet before scrolling content, when swiping up with multiple detents") {
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
                        initialDetent = halfExpandedDetent,
                        detents = listOf(halfExpandedDetent, SheetDetent.FullyExpanded)
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

            // Swipe up on the sheet - this should move the sheet to FullyExpanded first
            onNodeWithTag("sheet").performTouchInput {
                swipeUp()
            }
            awaitIdle()
            assertEquals(SheetDetent.FullyExpanded, state.currentDetent)
        }

        testCase("bottom of scrollable sheet is not clipped") {
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
                        initialDetent = halfExpandedDetent,
                        detents = listOf(SheetDetent.Hidden, halfExpandedDetent)
                    )

                    BottomSheet(
                        state = state,
                        modifier = Modifier
                            .background(Color.White)
                            .testTag("sheet")
                            .fillMaxHeight()
                    ) {
                        Column(Modifier.height(visibleHeight).verticalScroll(rememberScrollState())) {
                            repeat(20) { index ->
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

            // Swipe up to the last element of the sheet
            onNodeWithTag("sheet").performScrollToNode(hasTestTag("item_19"))
            awaitIdle()
            onNodeWithTag("item_19").assertIsDisplayed()
        }
    }

    @Test
    fun confirmDetentChange() = runTestSuite {
        testCase("detent change is blocked, when confirmDetentChange returns false for targetDetent") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200),
                    confirmDetentChange = { false }
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(300.dp))
                }
            }

            waitForIdle()
            state.targetDetent = detent200
            waitForIdle()

            assertThat(state.currentDetent).isEqualTo(detent100)
        }

        testCase("detent change proceeds, when confirmDetentChange returns true") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200),
                    confirmDetentChange = { true }
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(300.dp))
                }
            }

            waitForIdle()
            state.targetDetent = detent200
            waitForIdle()

            assertThat(state.currentDetent).isEqualTo(detent200)
        }

        testCase("sheet returns to original detent, when drag is blocked by confirmDetentChange") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200),
                    confirmDetentChange = { newDetent -> newDetent != detent200 }
                )

                BottomSheet(state, Modifier.testTag("sheet")) {
                    Box(Modifier.fillMaxWidth().height(300.dp))
                }
            }

            waitForIdle()
            val initialOffset = state.offset

            // Try to drag upward toward detent200
            onNodeWithTag("sheet").performTouchInput {
                swipeUp()
            }
            waitForIdle()

            // Should return to original detent
            assertThat(state.currentDetent).isEqualTo(detent100)
            assertThat(state.offset).isEqualTo(initialOffset)
        }

        testCase("programmatic detent change is blocked, when confirmDetentChange returns false for animateTo") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }

            lateinit var state: BottomSheetState
            lateinit var scope: CoroutineScope

            setContent {
                scope = rememberCoroutineScope()
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200),
                    confirmDetentChange = { newDetent -> newDetent != detent200 }
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(300.dp))
                }
            }

            waitForIdle()

            scope.launch {
                state.animateTo(detent200)
            }
            waitForIdle()

            assertThat(state.currentDetent).isEqualTo(detent100)
        }

        testCase("sheet stays at current detent, when confirmDetentChange blocks all other detents") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }
            val detent300 = SheetDetent("300") { _, _ -> 300.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent200,
                    detents = listOf(detent100, detent200, detent300),
                    confirmDetentChange = { newDetent -> newDetent == detent200 }
                )

                BottomSheet(state, Modifier.testTag("sheet")) {
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            waitForIdle()

            // Try to swipe up
            onNodeWithTag("sheet").performTouchInput { swipeUp() }
            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent200)

            // Try to swipe down
            onNodeWithTag("sheet").performTouchInput { swipeDown() }
            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent200)
        }

        testCase("confirmDetentChange is called with correct detent, when dragging upward") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }

            var receivedDetent: SheetDetent? = null
            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200),
                    confirmDetentChange = { newDetent ->
                        receivedDetent = newDetent
                        true
                    }
                )

                BottomSheet(state, Modifier.testTag("sheet")) {
                    Box(Modifier.fillMaxWidth().height(300.dp))
                }
            }

            waitForIdle()

            onNodeWithTag("sheet").performTouchInput { swipeUp() }
            waitForIdle()

            assertThat(receivedDetent).isEqualTo(detent200)
        }

        testCase("confirmDetentChange is called with correct detent, when using targetDetent setter") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }

            var receivedDetent: SheetDetent? = null
            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200),
                    confirmDetentChange = { newDetent ->
                        receivedDetent = newDetent
                        true
                    }
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(300.dp))
                }
            }

            waitForIdle()
            state.targetDetent = detent200
            waitForIdle()

            assertThat(receivedDetent).isEqualTo(detent200)
        }

        testCase("confirmDetentChange is called with correct detent, when using animateTo") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }

            var receivedDetent: SheetDetent? = null
            lateinit var state: BottomSheetState
            lateinit var scope: CoroutineScope

            setContent {
                scope = rememberCoroutineScope()
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200),
                    confirmDetentChange = { newDetent ->
                        receivedDetent = newDetent
                        true
                    }
                )

                BottomSheet(state) {
                    Box(Modifier.fillMaxWidth().height(300.dp))
                }
            }

            waitForIdle()

            scope.launch {
                state.animateTo(detent200)
            }
            waitForIdle()

            assertThat(receivedDetent).isEqualTo(detent200)
        }
    }

    @Test
    fun dragIndication() = runTestSuite {
        testCase("sheet moves to next detent up, when drag indication clicked from bottom") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }
            val detent300 = SheetDetent("300") { _, _ -> 300.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200, detent300)
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent100)

            onNodeWithTag("drag_indication").performClick()
            waitForIdle()

            assertThat(state.currentDetent).isEqualTo(detent200)
        }

        testCase("sheet moves to next detent down, when drag indication clicked from top") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }
            val detent300 = SheetDetent("300") { _, _ -> 300.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent300,
                    detents = listOf(detent100, detent200, detent300)
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent300)

            onNodeWithTag("drag_indication").performClick()
            waitForIdle()

            assertThat(state.currentDetent).isEqualTo(detent200)
        }

        testCase("drag indication is disabled, when only one detent") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100)
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            waitForIdle()
            onNodeWithTag("drag_indication").assertIsNotEnabled()
        }

        testCase("sheet cycles through all detents upward, when clicking drag indication multiple times") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }
            val detent300 = SheetDetent("300") { _, _ -> 300.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200, detent300)
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent100)

            // Click to move to detent200
            onNodeWithTag("drag_indication").performClick()
            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent200)

            // Click to move to detent300
            onNodeWithTag("drag_indication").performClick()
            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent300)
        }

        testCase("sheet reverses direction, when reaching top detent") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }
            val detent300 = SheetDetent("300") { _, _ -> 300.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent200,
                    detents = listOf(detent100, detent200, detent300)
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            waitForIdle()

            // Click to move up to detent300
            onNodeWithTag("drag_indication").performClick()
            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent300)

            // Click again - should reverse and go down to detent200
            onNodeWithTag("drag_indication").performClick()
            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent200)
        }

        testCase("sheet reverses direction, when reaching bottom detent") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }
            val detent300 = SheetDetent("300") { _, _ -> 300.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent200,
                    detents = listOf(detent100, detent200, detent300)
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            waitForIdle()

            // First move to top
            onNodeWithTag("drag_indication").performClick()
            waitForIdle()

            // Then start going down
            onNodeWithTag("drag_indication").performClick()
            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent200)

            onNodeWithTag("drag_indication").performClick()
            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent100)

            // Click again - should reverse and go up to detent200
            onNodeWithTag("drag_indication").performClick()
            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent200)
        }

        testCase("drag indication respects confirmDetentChange, when moving to blocked detent") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }
            val detent300 = SheetDetent("300") { _, _ -> 300.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200, detent300),
                    confirmDetentChange = { newDetent ->
                        newDetent != detent200
                    }
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent100)

            // Try to click - should be blocked from moving to detent200
            onNodeWithTag("drag_indication").performClick()
            waitForIdle()

            assertThat(state.currentDetent).isEqualTo(detent100)
        }

        testCase("drag indication stays at current detent, when next detent is blocked by confirmDetentChange") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }
            val detent300 = SheetDetent("300") { _, _ -> 300.dp }
            val detent400 = SheetDetent("400") { _, _ -> 400.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200, detent300, detent400),
                    confirmDetentChange = { newDetent ->
                        // Block detent200, allow all others
                        newDetent != detent200
                    }
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(500.dp))
                }
            }

            waitForIdle()
            assertThat(state.currentDetent).isEqualTo(detent100)

            // Click drag indication - detent200 is blocked, so sheet stays at detent100
            onNodeWithTag("drag_indication").performClick()
            waitForIdle()

            assertThat(state.currentDetent).isEqualTo(detent100)
        }

        testCase("drag indication has button role, when multiple detents exist") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }
            val detent200 = SheetDetent("200") { _, _ -> 200.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100, detent200)
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            waitForIdle()

            // Verify drag indication has button role for accessibility
            onNodeWithTag("drag_indication")
                .assertHasClickAction()
                .assertIsEnabled()
        }

        testCase("drag indication is not enabled, when only one detent exists") {
            val detent100 = SheetDetent("100") { _, _ -> 100.dp }

            lateinit var state: BottomSheetState

            setContent {
                state = rememberBottomSheetState(
                    initialDetent = detent100,
                    detents = listOf(detent100)
                )

                BottomSheet(state) {
                    DragIndication(Modifier.testTag("drag_indication").size(32.dp))
                    Box(Modifier.fillMaxWidth().height(400.dp))
                }
            }

            waitForIdle()

            // Verify drag indication is disabled when only one detent
            onNodeWithTag("drag_indication")
                .assertIsNotEnabled()
        }
    }

    // Helper functions for semantic assertions
    private fun hasExpandAction(): SemanticsMatcher {
        return SemanticsMatcher("has expand action") { node ->
            androidx.compose.ui.semantics.SemanticsActions.Expand in node.config
        }
    }

    private fun hasCollapseAction(): SemanticsMatcher {
        return SemanticsMatcher("has collapse action") { node ->
            androidx.compose.ui.semantics.SemanticsActions.Collapse in node.config
        }
    }

    private fun hasNoExpandAction(): SemanticsMatcher {
        return SemanticsMatcher("has no expand action") { node ->
            androidx.compose.ui.semantics.SemanticsActions.Expand !in node.config
        }
    }

    private fun hasNoCollapseAction(): SemanticsMatcher {
        return SemanticsMatcher("has no collapse action") { node ->
            androidx.compose.ui.semantics.SemanticsActions.Collapse !in node.config
        }
    }
}
