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
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.semantics.Role

private class TabsRegistry {
    var focusedTab: TabKey? by mutableStateOf(null)
    var activatedTab: TabKey? by mutableStateOf(null)

    var tabKeys: List<TabKey> by mutableStateOf(emptyList())
    var tabFocusRequesters: Map<TabKey, FocusRequester> by mutableStateOf(emptyMap())
    var panelsFocusRequesters: Map<TabKey, FocusRequester> by mutableStateOf(emptyMap())
}

private val LocalTabsRegistry = staticCompositionLocalOf { TabsRegistry() }

@Composable
fun TabGroup(
    selectedTab: TabKey,
    tabs: List<TabKey>,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val registry = remember(tabs) {
        val tabsRequesters = tabs.associateWith { FocusRequester() }
        val panelsRequesters = tabs.associateWith { FocusRequester() }
        TabsRegistry().apply {
            tabKeys = tabs
            tabFocusRequesters = tabsRequesters
            panelsFocusRequesters = panelsRequesters
        }
    }

    SideEffect { registry.activatedTab = selectedTab }

    Column(
        modifier = modifier
            .focusProperties {
                onEnter = {
                    val currentTab = registry.activatedTab
                    if (currentTab != null
                        && (requestedFocusDirection == FocusDirection.Next || requestedFocusDirection == FocusDirection.Previous)
                    ) {
                        // Using FocusDirection to know if we are moving due Tab press
                        registry.tabFocusRequesters.getValue(currentTab).requestFocus()
                    }
                }
            }
            .focusGroup()
    ) {
        CompositionLocalProvider(LocalTabsRegistry provides registry) {
            content()
        }
    }
}

@Composable
fun TabList(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = NoPadding,
    orientation: Orientation = Orientation.Horizontal,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    val registry = LocalTabsRegistry.current
    val tabKeys = registry.tabKeys

    Row(
        modifier = modifier
            .focusRestorer()
            .focusGroup()
            .selectableGroup()
            .clip(shape)
            .background(backgroundColor)
            .padding(contentPadding)
            .onKeyEvent { event ->
                when {
                    orientation == Orientation.Horizontal && event.key == Key.DirectionLeft -> {
                        if (event.isKeyDown) {
                            val currentIndex = tabKeys.indexOf(registry.focusedTab)

                            val nextIndex = (currentIndex - 1 + tabKeys.size) % tabKeys.size
                            val nextKey = tabKeys[nextIndex]

                            val focusRequester = registry.tabFocusRequesters[nextKey]!!
                            focusRequester.requestFocus()
                        }
                        true
                    }

                    orientation == Orientation.Horizontal && event.key == Key.DirectionRight -> {
                        if (event.isKeyDown) {
                            val currentIndex = tabKeys.indexOf(registry.focusedTab)

                            val nextIndex = (currentIndex + 1) % tabKeys.size
                            val nextKey = tabKeys[nextIndex]

                            val focusRequester = registry.tabFocusRequesters[nextKey]!!
                            focusRequester.requestFocus()
                        }
                        true
                    }

                    orientation == Orientation.Vertical && event.key == Key.DirectionUp -> {
                        if (event.isKeyDown) {
                            val currentIndex = tabKeys.indexOf(registry.focusedTab)

                            val nextIndex = (currentIndex - 1 + tabKeys.size) % tabKeys.size
                            val nextKey = tabKeys[nextIndex]

                            val focusRequester = registry.tabFocusRequesters[nextKey]!!
                            focusRequester.requestFocus()
                        }
                        true
                    }

                    orientation == Orientation.Vertical && event.key == Key.DirectionDown -> {
                        if (event.isKeyDown) {
                            val currentIndex = tabKeys.indexOf(registry.focusedTab)

                            val nextIndex = (currentIndex + 1) % tabKeys.size
                            val nextKey = tabKeys[nextIndex]

                            val focusRequester = registry.tabFocusRequesters[nextKey]!!
                            focusRequester.requestFocus()
                        }
                        true
                    }

                    event.key == Key.Home -> {
                        if (event.isKeyDown) {
                            val tab = tabKeys.first()
                            val focusRequester = registry.tabFocusRequesters.getValue(tab)
                            focusRequester.requestFocus()
                        }
                        true
                    }

                    event.key == Key.MoveEnd -> {
                        if (event.isKeyDown) {
                            val tab = tabKeys.last()
                            val focusRequester = registry.tabFocusRequesters.getValue(tab)
                            focusRequester.requestFocus()
                        }
                        true
                    }


                    else -> false
                }
            }
            .onFocusChanged {
                if (it.hasFocus && registry.focusedTab == null) {
                    registry.tabFocusRequesters[tabKeys.firstOrNull()]?.requestFocus()
                }
            },
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}

@Composable
fun Tab(
    key: TabKey,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    activateOnFocus: Boolean = true,
    indication: Indication = LocalIndication.current,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = NoPadding,
    content: @Composable () -> Unit
) {
    val registry = LocalTabsRegistry.current
    val focusRequester = registry.tabFocusRequesters[key]
        ?: error("Tried to setup a Tab with key = $key but was not found in the tab keys. Make sure you provide the key")
    val activatedTab = registry.activatedTab

    Box(
        modifier = modifier
            .focusRequester(focusRequester)
            .focusProperties {
                next = registry.panelsFocusRequesters[activatedTab] ?: FocusRequester.Default
            }
            .onFocusChanged {
                if (it.isFocused) {
                    registry.focusedTab = key
                    if (activateOnFocus) {
                        onSelected()
                    }
                }
            }
            .clip(shape)
            .background(backgroundColor)
            .selectable(
                selected = selected,
                onClick = onSelected,
                indication = indication,
                interactionSource = interactionSource,
                enabled = enabled,
                role = Role.Tab
            )
            .padding(contentPadding),
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}

@Composable
fun TabPanel(
    key: TabKey,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    contentPadding: PaddingValues = NoPadding,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    val registry = LocalTabsRegistry.current

    if (registry.activatedTab == key) {
        val focusRequester = registry.panelsFocusRequesters[key]
            ?: error("Tried to activate TabPanel with key = $key. Did you forget to pass the key in the list of tabs in your TabGroup?")

        Box(
            modifier = modifier
                .clip(shape)
                .background(backgroundColor)
                .padding(contentPadding)
                .focusRequester(focusRequester)
                .focusGroup(),
            contentAlignment = contentAlignment,
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                content()
            }
        }
    }
}
