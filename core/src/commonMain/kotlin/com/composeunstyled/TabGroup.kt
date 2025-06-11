package com.composeunstyled

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role

/**
 * A unique identifier for a tab in a [TabGroup].
 */
typealias TabKey = String

class TabGroupState(initialTab: TabKey, internal val tabs: List<TabKey>) {
    var selectedTab by mutableStateOf(initialTab)
    var focusedTab by mutableStateOf<TabKey?>(null)
}

/**
 * Creates a [TabGroupState].
 *
 * @param selectedTab The initially selected tab.
 * @param orderedTabs The list of tabs in the order they should appear.
 */
@Composable
fun rememberTabGroupState(selectedTab: TabKey, orderedTabs: List<TabKey>): TabGroupState {
    return remember {
        TabGroupState(
            initialTab = selectedTab,
            tabs = orderedTabs,
        )
    }
}

class TabGroupScope(val state: TabGroupState) {
    internal val tabFocusRequesters = mutableMapOf<TabKey, FocusRequester>()
    internal val panelFocusRequesters = mutableMapOf<TabKey, FocusRequester>()
}

/**
 * A foundational component used to build tabbed interfaces.
 *
 * For interactive preview & code examples, visit [Tab Group Documentation](https://composeunstyled.com/tabgroup).
 *
 * ## Basic Example
 *
 * ```kotlin
 * val tabState = rememberTabGroupState("tab1", listOf("tab1", "tab2", "tab3"))
 *
 * TabGroup(state = tabState) {
 *     TabList {
 *         Tab(key = "tab1") {
 *             Text("Tab 1")
 *         }
 *         Tab(key = "tab2") {
 *             Text("Tab 2")
 *         }
 *     }
 *     TabPanel(key = "tab1") {
 *         Text("Content 1")
 *     }
 *     TabPanel(key = "tab2") {
 *         Text("Content 2")
 *     }
 * }
 * ```
 *
 * @param state The [TabGroupState] that controls the tab group.
 * @param modifier Modifier to be applied to the tab group.
 * @param content The content of the tab group.
 */
@Composable
fun TabGroup(
    state: TabGroupState,
    modifier: Modifier = Modifier,
    content: @Composable TabGroupScope.() -> Unit
) {
    val scope = remember { TabGroupScope(state) }

    Column(modifier) {
        with(scope) {
            content()
        }
    }
}

/**
 * A container holding all [Tab]s that can be selected withing a [TabGroup].
 *
 * @param modifier Modifier to be applied to the tab list.
 * @param shape The shape of the tab list.
 * @param backgroundColor The background color of the tab list.
 * @param orientation The orientation of the tab list.
 * @param activateOnFocus Whether to activate a tab when it receives focus.
 * @param content The content of the tab list.
 */
@Composable
fun TabGroupScope.TabList(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    orientation: Orientation = Orientation.Horizontal,
    activateOnFocus: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier
            .selectableGroup()
            .onFocusChanged {
                if (it.hasFocus) {
                    val focusRequester = tabFocusRequesters[state.selectedTab]
                    focusRequester?.requestFocus()
                }
            }
            .focusGroup()
            .onKeyEvent { event ->

                fun moveFocusTo(key: TabKey, activate: Boolean = activateOnFocus) {
                    tabFocusRequesters.getValue(key).requestFocus()
                    if (activate) {
                        state.selectedTab = key
                    }
                }

                fun moveFocusToPrevious() {
                    val focusedIndex = state.tabs.indexOf(state.focusedTab)
                    val previous = if (focusedIndex == 0) {
                        state.tabs.last()
                    } else {
                        state.tabs[focusedIndex - 1]
                    }
                    moveFocusTo(previous)
                }

                fun moveFocusToNext() {
                    val focusedIndex = state.tabs.indexOf(state.focusedTab)
                    val next = if (focusedIndex == state.tabs.lastIndex) {
                        state.tabs.first()
                    } else {
                        state.tabs[focusedIndex + 1]
                    }
                    moveFocusTo(next)
                }

                when (event.key) {
                    Key.Tab -> {
                        if (event.isKeyDown) {
                            if (event.isShiftPressed) {
                                // move to item before the TabList
                                moveFocusTo(state.tabs.first(), activate = false)
                                focusManager.moveFocus(FocusDirection.Previous)
                            } else {
                                // move to item after TabList
                                moveFocusTo(state.tabs.last(), activate = false)
                                focusManager.moveFocus(FocusDirection.Next)
                            }
                        }
                        true
                    }

                    Key.Home -> {
                        if (event.isKeyDown) {
                            moveFocusTo(state.tabs.first())
                        }
                        true
                    }

                    Key.MoveEnd -> {
                        if (event.isKeyDown) {
                            moveFocusTo(state.tabs.last())
                        }
                        true
                    }

                    Key.DirectionRight -> {
                        if (orientation == Orientation.Horizontal) {
                            if (event.isKeyDown) {
                                moveFocusToNext()
                            }
                            true
                        } else {
                            false
                        }
                    }

                    Key.DirectionDown -> {
                        if (orientation == Orientation.Vertical) {
                            if (event.isKeyDown) {
                                moveFocusToNext()
                            }
                            true
                        } else {
                            false
                        }
                    }

                    Key.DirectionLeft -> {
                        if (orientation == Orientation.Horizontal) {
                            if (event.isKeyDown) {
                                moveFocusToPrevious()
                            }
                            true
                        } else {
                            false
                        }
                    }

                    Key.DirectionUp -> {
                        if (orientation == Orientation.Vertical) {
                            if (event.isKeyDown) {
                                moveFocusToPrevious()
                            }
                            true
                        } else {
                            false
                        }
                    }

                    else -> false
                }
            }
            .clip(shape)
            .background(backgroundColor),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

/**
 * A single tab in a [TabList].
 *
 * @param key The unique identifier for this tab.
 * @param modifier Modifier to be applied to the tab.
 * @param enabled Whether the tab is enabled.
 * @param indication The indication to be shown when the tab is interacted with.
 * @param interactionSource The interaction source for the tab.
 * @param content The content of the tab.
 */
@Composable
fun TabGroupScope.Tab(
    key: TabKey,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    indication: Indication = LocalIndication.current,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
) {
    val focusRequester = remember(key) { FocusRequester() }

    DisposableEffect(key) {
        tabFocusRequesters[key] = focusRequester
        onDispose {
            tabFocusRequesters.remove(key)
        }
    }

    Box(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    state.focusedTab = key
                }
            }
            .selectable(
                selected = state.selectedTab == key,
                onClick = { state.selectedTab = key },
                indication = indication,
                interactionSource = interactionSource,
                enabled = enabled,
                role = Role.Tab
            )
    ) {
        content()
    }
}

/**
 * The content panel for a tab.
 *
 * @param key The unique identifier for the tab this panel belongs to.
 * @param content The content of the panel.
 */
@Composable
fun TabGroupScope.TabPanel(key: TabKey, content: @Composable () -> Unit) {
    val focusRequester = remember(key) { FocusRequester() }

    DisposableEffect(key) {
        panelFocusRequesters[key] = focusRequester
        onDispose {
            panelFocusRequesters.remove(key)
        }
    }

    if (key == state.selectedTab) {
        Box(Modifier.focusRequester(focusRequester)) {
            content()
        }
    }
}
