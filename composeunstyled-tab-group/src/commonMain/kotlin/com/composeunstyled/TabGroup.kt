/*
 * Copyright (c) 2026 Composable Horizons
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
@file:Suppress("ktlint:standard:max-line-length")

package com.composeunstyled

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

typealias TabKey = String

private val NoPadding = PaddingValues(0.dp)

private val KeyEvent.isKeyDown: Boolean
  get() = type == KeyEventType.KeyDown

internal class TabsRegistry<T>(
  val onSelectedTabChange: (T) -> Unit = {},
) {
  var focusedTab: T? by mutableStateOf(null)
  var activatedTab: T? by mutableStateOf(null)
  var isMovingFocusBackwardOutOfTabList: Boolean by mutableStateOf(false)

  var tabKeys: List<T> by mutableStateOf(emptyList())
  var tabFocusRequesters: Map<T, FocusRequester> by mutableStateOf(emptyMap())
  var panelsFocusRequesters: Map<T, FocusRequester> by mutableStateOf(emptyMap())
}

class TabGroupScope<T> internal constructor(
  internal val registry: TabsRegistry<T>,
)

class TabListScope<T> internal constructor(
  internal val registry: TabsRegistry<T>,
)

class TabScope internal constructor(
  val selected: Boolean,
  val enabled: Boolean,
)

@Composable
fun <T> UnstyledTabGroup(
  selectedTab: T,
  onSelectedTabChange: (T) -> Unit,
  tabs: List<T>,
  modifier: Modifier = Modifier,
  content: @Composable TabGroupScope<T>.() -> Unit,
) {
  val registry = remember(tabs, onSelectedTabChange) {
    val tabsRequesters = tabs.associateWith { FocusRequester() }
    val panelsRequesters = tabs.associateWith { FocusRequester() }
    TabsRegistry(onSelectedTabChange = onSelectedTabChange).apply {
      tabKeys = tabs
      tabFocusRequesters = tabsRequesters
      panelsFocusRequesters = panelsRequesters
    }
  }

  SideEffect { registry.activatedTab = selectedTab }

  Box(
    modifier = modifier
      .focusProperties {
        onEnter = {
          val currentTab = registry.activatedTab
          if (currentTab != null &&
            (requestedFocusDirection == FocusDirection.Next || requestedFocusDirection == FocusDirection.Previous)
          ) {
            // Using FocusDirection to know if we are moving due Tab press
            registry.tabFocusRequesters.getValue(currentTab).requestFocus()
          }
        }
      }
      .focusGroup(),
  ) {
    val tabGroupScope = remember(registry) {
      TabGroupScope(registry)
    }
    tabGroupScope.content()
  }
}

@Composable
fun <T> TabGroupScope<T>.TabList(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = NoPadding,
  orientation: Orientation = Orientation.Horizontal,
  content: @Composable TabListScope<T>.() -> Unit,
) {
  val registry = registry
  val tabKeys = registry.tabKeys

  Box(
    modifier = modifier
      .focusRestorer()
      .focusGroup()
      .selectableGroup()
      .padding(contentPadding)
      .onKeyEvent { event ->
        when {
          orientation == Orientation.Horizontal && event.key == Key.DirectionLeft -> {
            if (event.isKeyDown && tabKeys.isNotEmpty()) {
              val currentIndex = tabKeys.indexOf(registry.focusedTab)

              val nextIndex = (currentIndex - 1 + tabKeys.size) % tabKeys.size
              val nextKey = tabKeys[nextIndex]

              val focusRequester = registry.tabFocusRequesters[nextKey]!!
              focusRequester.requestFocus()
            }
            true
          }

          orientation == Orientation.Horizontal && event.key == Key.DirectionRight -> {
            if (event.isKeyDown && tabKeys.isNotEmpty()) {
              val currentIndex = tabKeys.indexOf(registry.focusedTab)

              val nextIndex = (currentIndex + 1) % tabKeys.size
              val nextKey = tabKeys[nextIndex]

              val focusRequester = registry.tabFocusRequesters[nextKey]!!
              focusRequester.requestFocus()
            }
            true
          }

          orientation == Orientation.Vertical && event.key == Key.DirectionUp -> {
            if (event.isKeyDown && tabKeys.isNotEmpty()) {
              val currentIndex = tabKeys.indexOf(registry.focusedTab)

              val nextIndex = (currentIndex - 1 + tabKeys.size) % tabKeys.size
              val nextKey = tabKeys[nextIndex]

              val focusRequester = registry.tabFocusRequesters[nextKey]!!
              focusRequester.requestFocus()
            }
            true
          }

          orientation == Orientation.Vertical && event.key == Key.DirectionDown -> {
            if (event.isKeyDown && tabKeys.isNotEmpty()) {
              val currentIndex = tabKeys.indexOf(registry.focusedTab)

              val nextIndex = (currentIndex + 1) % tabKeys.size
              val nextKey = tabKeys[nextIndex]

              val focusRequester = registry.tabFocusRequesters[nextKey]!!
              focusRequester.requestFocus()
            }
            true
          }

          event.key == Key.Home -> {
            if (event.isKeyDown && tabKeys.isNotEmpty()) {
              val tab = tabKeys.first()
              val focusRequester = registry.tabFocusRequesters.getValue(tab)
              focusRequester.requestFocus()
            }
            true
          }

          event.key == Key.MoveEnd -> {
            if (event.isKeyDown && tabKeys.isNotEmpty()) {
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
  ) {
    val tabListScope = remember(registry) {
      TabListScope(registry)
    }
    tabListScope.content()
  }
}

@Composable
fun <T> TabListScope<T>.Tab(
  key: T,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  activateOnFocus: Boolean = true,
  indication: Indication? = LocalIndication.current,
  interactionSource: MutableInteractionSource? = null,
  contentPadding: PaddingValues = NoPadding,
  content: @Composable TabScope.() -> Unit,
) {
  val registry = registry
  val focusManager = LocalFocusManager.current
  val focusRequester = registry.tabFocusRequesters[key] ?: FocusRequester.Default
  val activatedTab = registry.activatedTab
  val selected = activatedTab == key
  val tabScope = remember(selected, enabled) {
    TabScope(
      selected = selected,
      enabled = enabled,
    )
  }

  Box(
    modifier = modifier
      .focusRequester(focusRequester)
      .onPreviewKeyEvent { event ->
        if (event.isKeyDown && event.key == Key.Tab && event.isShiftPressed) {
          registry.isMovingFocusBackwardOutOfTabList = true
          try {
            focusManager.moveFocus(FocusDirection.Previous)
          } finally {
            registry.isMovingFocusBackwardOutOfTabList = false
          }
          true
        } else {
          false
        }
      }
      .focusProperties {
        if (registry.isMovingFocusBackwardOutOfTabList && registry.focusedTab != key) {
          canFocus = false
        }
        next = registry.panelsFocusRequesters[activatedTab] ?: FocusRequester.Default
      }
      .onFocusChanged {
        if (it.isFocused) {
          registry.focusedTab = key
          if (activateOnFocus) {
            registry.onSelectedTabChange(key)
          }
        }
      }
      .selectable(
        selected = selected,
        onClick = { registry.onSelectedTabChange(key) },
        indication = indication,
        interactionSource = interactionSource,
        enabled = enabled,
        role = Role.Tab,
      )
      .padding(contentPadding),
  ) {
    tabScope.content()
  }
}

@Composable
fun <T> TabGroupScope<T>.TabPanel(
  key: T,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = NoPadding,
  content: @Composable () -> Unit,
) {
  val registry = registry

  if (registry.activatedTab == key) {
    val focusRequester = registry.panelsFocusRequesters[key] ?: FocusRequester.Default

    Box(
      modifier = modifier
        .padding(contentPadding)
        .focusRequester(focusRequester)
        .focusable()
        .focusGroup(),
    ) {
      content()
    }
  }
}
