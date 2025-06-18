package com.composables.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import com.composeunstyled.*
import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat

@OptIn(ExperimentalTestApi::class)
class TabGroupTest {
    @Test
    fun givenFocusIsOutsideOfTabList_whenFocusIsMovedIntoTabList_thenMovesFocusToFistTab() = runComposeUiTest {
        var selectedTab by mutableStateOf("tab1")

        setContent {
            Button(onClick = {}, modifier = Modifier.testFocusTag("button")) {
                Text("Outside")
            }

            TabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
                TabList(Modifier.testFocusTag("tablist")) {
                    Tab(
                        key = "tab1",
                        modifier = Modifier.testFocusTag("tab1"),
                        selected = selectedTab == "tab1",
                        onSelected = { selectedTab = "tab1" }
                    ) {
                        Text("Tab #1")
                    }
                    Tab(
                        key = "tab2",
                        modifier = Modifier.testFocusTag("tab2"),
                        selected = selectedTab == "tab2",
                        onSelected = { selectedTab = "tab2" }
                    ) {
                        Text("Tab #2")
                    }
                    Tab(
                        key = "tab3",
                        modifier = Modifier.testFocusTag("tab3"),
                        selected = selectedTab == "tab3",
                        onSelected = { selectedTab = "tab3" }
                    ) {
                        Text("Tab #3")
                    }
                }
            }
        }
        onNodeWithTag("button").requestFocus()
        onRoot().performKeyInput {
            keyPress(Key.Tab)
        }
        onNodeWithTag("tab1").assertIsFocused()
    }

    @Test
    fun givenFocusIsOutsideOfTabList_whenFocusIsMovedIntoTabList_thenMovesFocusToActivatedTab() = runComposeUiTest {
        var selectedTab by mutableStateOf("tab2")

        setContent {
            Button(onClick = {}, modifier = Modifier.testFocusTag("button")) {
                Text("Outside")
            }

            TabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
                TabList(Modifier.testFocusTag("tablist")) {
                    Tab(
                        key = "tab1",
                        modifier = Modifier.testFocusTag("tab1"),
                        selected = selectedTab == "tab1",
                        onSelected = { selectedTab = "tab1" }
                    ) {
                        Text("Tab #1")
                    }
                    Tab(
                        key = "tab2",
                        modifier = Modifier.testFocusTag("tab2"),
                        selected = selectedTab == "tab2",
                        onSelected = { selectedTab = "tab2" }
                    ) {
                        Text("Tab #2")
                    }
                    Tab(
                        key = "tab3",
                        modifier = Modifier.testFocusTag("tab3"),
                        selected = selectedTab == "tab3",
                        onSelected = { selectedTab = "tab3" }
                    ) {
                        Text("Tab #3")
                    }
                }
            }
        }
        onNodeWithTag("button").requestFocus()
        onRoot().performKeyInput {
            keyPress(Key.Tab)
        }
        onNodeWithTag("tab2").assertIsFocused()
    }


    @Test
    fun givenTabListHasFocus_whenPressingRightArrowKey_thenMovesFocusToSecondTab() = runComposeUiTest {
        var selectedTab by mutableStateOf("tab1")

        setContent {
            Button(onClick = {}, modifier = Modifier.testFocusTag("button")) {
                Text("Outside")
            }
            TabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
                TabList(Modifier.testFocusTag("tablist")) {
                    Tab(
                        "tab1",
                        modifier = Modifier.testFocusTag("tab1"),
                        selected = selectedTab == "tab1",
                        onSelected = { selectedTab = "tab1" }) {
                        Text("Tab #1")
                    }
                    Tab(
                        "tab2",
                        modifier = Modifier.testFocusTag("tab2"),
                        selected = selectedTab == "tab2",
                        onSelected = { selectedTab = "tab2" }) {
                        Text("Tab #2")
                    }
                    Tab(
                        "tab3",
                        modifier = Modifier.testFocusTag("tab3"),
                        selected = selectedTab == "tab3",
                        onSelected = { selectedTab = "tab3" }) {
                        Text("Tab #3")
                    }
                }
            }
        }
        onNodeWithTag("tab1").requestFocus()
        onNodeWithTag("tab1").assertIsFocused()

        onRoot().performKeyInput {
            keyPress(Key.DirectionRight)
        }
        onNodeWithTag("tab2").assertIsFocused()
    }

    @Test
    fun givenTabListHasFocus_whenPressingLeftArrowKey_thenMovesFocusToLastTab() = runComposeUiTest {
        var selectedTab by mutableStateOf("tab1")

        setContent {
            Button(onClick = {}, modifier = Modifier.testFocusTag("button").testFocusTag("button")) {
                Text("Outside")
            }

            TabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
                TabList(Modifier.testFocusTag("tablist").testFocusTag("tablist")) {
                    Tab(
                        "tab1",
                        modifier = Modifier.testFocusTag("tab1").testFocusTag("tab1"),
                        selected = selectedTab == "tab1",
                        onSelected = { selectedTab = "tab1" }) {
                        Text("Tab #1")
                    }
                    Tab(
                        "tab2",
                        modifier = Modifier.testFocusTag("tab2").testFocusTag("tab2"),
                        selected = selectedTab == "tab2",
                        onSelected = { selectedTab = "tab2" }) {
                        Text("Tab #2")
                    }
                    Tab(
                        "tab3",
                        modifier = Modifier.testFocusTag("tab3").testFocusTag("tab3"),
                        selected = selectedTab == "tab3",
                        onSelected = { selectedTab = "tab3" }) {
                        Text("Tab #3")
                    }
                }
            }
        }
        onNodeWithTag("tab1").requestFocus()
        onRoot().performKeyInput {
            keyPress(Key.DirectionLeft)
        }
        onNodeWithTag("tab3").assertIsFocused()
    }

    @Test
    fun givenTabHasFocus_whenPressingTab_thenMovesFocusToRespectiveTabPanel() = runComposeUiTest {
        var selectedTab by mutableStateOf("tab1")

        setContent {
            TabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
                TabList(Modifier.testFocusTag("tablist")) {
                    Tab(
                        key = "tab1",
                        modifier = Modifier.testFocusTag("tab1"),
                        selected = selectedTab == "tab1",
                        onSelected = { selectedTab = "tab1" }) {
                        Text("Tab #1")
                    }
                    Tab(
                        key = "tab2",
                        modifier = Modifier.testFocusTag("tab2"),
                        selected = selectedTab == "tab2",
                        onSelected = { selectedTab = "tab2" }) {
                        Text("Tab #2")
                    }
                    Tab(
                        key = "tab3",
                        modifier = Modifier.testFocusTag("tab3"),
                        selected = selectedTab == "tab3",
                        onSelected = { selectedTab = "tab3" }
                    ) {
                        Text("Tab #3")
                    }
                }
                TabPanel(key = "tab1") {
                    Button(onClick = {}, modifier = Modifier.testFocusTag("panelA")) {
                        Text("Panel A")
                    }
                }
                TabPanel(key = "tab2") {
                    Button(onClick = {}, modifier = Modifier.testFocusTag("panelB")) {
                        Text("Panel B")
                    }
                }
                TabPanel(key = "tab3") {
                    Button(onClick = {}, modifier = Modifier.testFocusTag("panelC")) {
                        Text("Panel C")
                    }
                }
            }
        }
        onNodeWithTag("tab1").requestFocus()
        onRoot().performKeyInput {
            keyPress(Key.Tab)
        }
        onNodeWithTag("panelA").isDisplayed()
        onNodeWithTag("panelA").assertIsFocused()
        onNodeWithTag("panelB").assertDoesNotExist()
        onNodeWithTag("panelC").assertDoesNotExist()
    }


    @Test
    fun canFocusSecondTabDirectly() = runComposeUiTest {
        var selectedTab by mutableStateOf("tab2")

        setContent {
            TabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
                TabList(Modifier.testFocusTag("tablist")) {
                    Tab(
                        "tab1",
                        modifier = Modifier.testFocusTag("tab1"),
                        selected = selectedTab == "tab1",
                        onSelected = { selectedTab = "tab1" }) {
                        Text("Tab #1")
                    }
                    Tab(
                        "tab2",
                        modifier = Modifier.testFocusTag("tab2"),
                        selected = selectedTab == "tab2",
                        onSelected = { selectedTab = "tab2" }) {
                        Text("Tab #2")
                    }
                    Tab(
                        "tab3",
                        modifier = Modifier.testFocusTag("tab3"),
                        selected = selectedTab == "tab3",
                        onSelected = { selectedTab = "tab3" }) {
                        Text("Tab #3")
                    }
                }
            }
        }

        onNodeWithTag("tab2").requestFocus()
        onNodeWithTag("tab2").assertIsFocused()
    }


    @Test
    fun givenTabHasFocus_whenPressingHomeKey_thenMovesFocusToFirstTab() = runComposeUiTest {
        var selectedTab by mutableStateOf("tab1")

        setContent {
            TabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
                TabList(Modifier.testFocusTag("tablist")) {
                    Tab(
                        "tab1",
                        modifier = Modifier.testFocusTag("tab1"),
                        selected = selectedTab == "tab1",
                        onSelected = { selectedTab = "tab1" }) {
                        Text("Tab #1")
                    }
                    Tab(
                        "tab2",
                        modifier = Modifier.testFocusTag("tab2"),
                        selected = selectedTab == "tab2",
                        onSelected = { selectedTab = "tab2" }) {
                        Text("Tab #2")
                    }
                    Tab(
                        "tab3",
                        modifier = Modifier.testFocusTag("tab3"),
                        selected = selectedTab == "tab3",
                        onSelected = { selectedTab = "tab3" }) {
                        Text("Tab #3")
                    }
                }
            }
        }

        // Focus on the second tab first
        onNodeWithTag("tab2").performClick()
        onNodeWithTag("tab2").assertIsFocused()

        // Press Home key
        onRoot().performKeyInput {
            keyPress(Key.Home)
        }

        // Should move focus to the first tab
        onNodeWithTag("tab1").assertIsFocused()
    }

    @Test
    fun givenTabHasFocus_whenPressingEndKey_thenMovesFocusToLastTab() = runComposeUiTest {
        var selectedTab by mutableStateOf("tab1")

        setContent {
            TabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
                TabList(Modifier.testFocusTag("tablist")) {
                    Tab(
                        "tab1",
                        modifier = Modifier.testFocusTag("tab1"),
                        selected = selectedTab == "tab1",
                        onSelected = { selectedTab = "tab1" }) {
                        Text("Tab #1")
                    }
                    Tab(
                        "tab2",
                        modifier = Modifier.testFocusTag("tab2"),
                        selected = selectedTab == "tab2",
                        onSelected = { selectedTab = "tab2" }) {
                        Text("Tab #2")
                    }
                    Tab(
                        "tab3",
                        modifier = Modifier.testFocusTag("tab3"),
                        selected = selectedTab == "tab3",
                        onSelected = { selectedTab = "tab3" }) {
                        Text("Tab #3")
                    }
                }
            }
        }

        // Focus on the second tab first
        onNodeWithTag("tab2").requestFocus()
        onNodeWithTag("tab2").assertIsFocused()

        // Press Home key
        onRoot().performKeyInput {
            keyPress(Key.MoveEnd)
        }

        // Should move focus to the first tab
        onNodeWithTag("tab3").assertIsFocused()

    }

    @Test
    fun givenTabHasFocus_whenPressingEnterKey_thenActivatesTab() = runComposeUiTest {
        var selectedTab by mutableStateOf("tab1")

        setContent {
            TabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
                TabList(Modifier.testFocusTag("tablist")) {
                    Tab(
                        key = "tab1",
                        modifier = Modifier.testFocusTag("tab1"),
                        selected = selectedTab == "tab1",
                        onSelected = { selectedTab = "tab1" }) {
                        Text("Tab #1")
                    }
                    Tab(
                        key = "tab2",
                        modifier = Modifier.testFocusTag("tab2"),
                        selected = selectedTab == "tab2",
                        onSelected = { selectedTab = "tab2" }
                    ) {
                        Text("Tab #2")
                    }
                    Tab(
                        key = "tab3",
                        modifier = Modifier.testFocusTag("tab3"),
                        selected = selectedTab == "tab3",
                        onSelected = { selectedTab = "tab3" }
                    ) {
                        Text("Tab #3")
                    }
                }
            }
        }

        // Focus on the second tab first
        onNodeWithTag("tab2").requestFocus()
        onNodeWithTag("tab2").assertIsFocused()

        // Press Home key
        onRoot().performKeyInput {
            keyPress(Key.Enter)
        }

        assertThat(selectedTab).isEqualTo("tab2")
    }

    @Test
    fun givenTabHasFocus_whenPressingSpaceKey_thenActivatesTabAndShowsPanel() = runComposeUiTest {
        // TODO: Test that pressing Space key activates the tab and causes its associated panel to be displayed
        // According to W3C: "activates the tab, causing its associated panel to be displayed"
    }

    @Test
    fun showsPanelOfTheActivatedTab() = runComposeUiTest {
        var selectedTab by mutableStateOf("tab1")

        println("Activated tab: $selectedTab")

        setContent {
            TabGroup(
                selectedTab = selectedTab,
                tabs = listOf("tab1", "tab2", "tab3"),
            ) {
                TabList(Modifier.testFocusTag("tablist")) {
                    Tab(
                        key = "tab1",
                        modifier = Modifier.testFocusTag("tab1"),
                        selected = selectedTab == "tab1",
                        onSelected = { selectedTab = "tab1" }
                    ) {
                        Text("Tab #1")
                    }
                    Tab(
                        key = "tab2",
                        modifier = Modifier.testFocusTag("tab2"),
                        selected = selectedTab == "tab2",
                        onSelected = { selectedTab = "tab2" }
                    ) {
                        Text("Tab #2")
                    }
                    Tab(
                        key = "tab3",
                        modifier = Modifier.testFocusTag("tab3"),
                        selected = selectedTab == "tab3",
                        onSelected = { selectedTab = "tab3" }
                    ) {
                        Text("Tab #3")
                    }
                }
                TabPanel(key = "tab1", Modifier.testFocusTag("panelA")) {
                    Text("Panel A")
                }
                TabPanel(key = "tab2", Modifier.testFocusTag("panelB")) {
                    Text("Panel B")
                }
                TabPanel(key = "tab3", Modifier.testFocusTag("panelC")) {
                    Text("Panel C")
                }
            }
        }

        onNodeWithTag("tab1").performClick()
        onNodeWithTag("panelA").isDisplayed()
        onNodeWithTag("panelB").assertDoesNotExist()
        onNodeWithTag("panelC").assertDoesNotExist()

        onNodeWithTag("tab2").performClick()
        onNodeWithTag("panelA").assertDoesNotExist()
        onNodeWithTag("panelB").isDisplayed()
        onNodeWithTag("panelC").assertDoesNotExist()

        onNodeWithTag("tab3").performClick()
        onNodeWithTag("panelA").assertDoesNotExist()
        onNodeWithTag("panelB").assertDoesNotExist()
        onNodeWithTag("panelC").isDisplayed()
    }

    @Test
    fun givenTabReceivesFocus_whenActivateOnFocusIsTrue_thenActivatesTab() = runComposeUiTest {
        var selectedTab by mutableStateOf("tab1")

        setContent {
            TabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
                TabList(Modifier.testFocusTag("tablist")) {
                    Tab(
                        key = "tab1",
                        modifier = Modifier.testFocusTag("tab1"),
                        selected = selectedTab == "tab1",
                        onSelected = { selectedTab = "tab1" },
                        activateOnFocus = true
                    ) {
                        Text("Tab #1")
                    }
                    Tab(
                        key = "tab2",
                        modifier = Modifier.testFocusTag("tab2"),
                        selected = selectedTab == "tab2",
                        onSelected = { selectedTab = "tab2" },
                        activateOnFocus = true
                    ) {
                        Text("Tab #2")
                    }
                    Tab(
                        key = "tab3",
                        modifier = Modifier.testFocusTag("tab3"),
                        selected = selectedTab == "tab3",
                        onSelected = { selectedTab = "tab3" },
                        activateOnFocus = true
                    ) {
                        Text("Tab #3")
                    }
                }
            }
        }

        // Focus on the second tab using arrow key navigation
        onNodeWithTag("tab1").requestFocus()
        onRoot().performKeyInput {
            keyPress(Key.DirectionRight)
        }

        // Verify that the second tab is focused and activated
        onNodeWithTag("tab2").assertIsFocused()
        assertThat(selectedTab).isEqualTo("tab2")
    }

    fun KeyInjectionScope.keyPress(key: Key) {
        println("👇 Key Down: $key")
        keyDown(key)
        println("👆 Key Up: $key")
        keyUp(key)
    }

    fun Modifier.testFocusTag(key: String): Modifier {
        return this.testTag(key).reportFocus(key)
    }

    private fun Modifier.reportFocus(key: String): Modifier {
        return this.onFocusChanged {
            if (it.hasFocus) {
                println("[$key] has focus")
            }
        }
    }
}
