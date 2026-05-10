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
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@file:Suppress("unused", "UNUSED_PARAMETER")

package com.composeunstyled.demo.material3api

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.composeunstyled.TabKey
import com.composeunstyled.TabList
import com.composeunstyled.TabListScope
import com.composeunstyled.UnstyledTabGroup
import kotlinx.coroutines.launch
import com.composeunstyled.Tab as UnstyledTab

private val PrimaryTabHeight = 48.dp
private val PrimaryTabIndicatorHeight = 3.dp
private val SecondaryTabIndicatorHeight = 3.dp
private val TabDividerHeight = 1.dp
private val TabHorizontalPadding = 16.dp
private val TabIconSize = 24.dp
private val TabIconTextPadding = 8.dp
private class MaterialTabRowContext(
  private val tabKeys: List<TabKey>,
) {
  var nextIndex = 0
  private val tabContentWidths = mutableStateMapOf<TabKey, Dp>()
  private val tabSelectionCallbacks = mutableStateMapOf<TabKey, () -> Unit>()

  fun nextTabKey(): TabKey {
    val index = nextIndex++
    return tabKeys.getOrNull(index)
      ?: error("PrimaryTabRow received more tabs than tabKeys. Add a stable key for every tab.")
  }

  fun setContentWidth(tabKey: TabKey, width: Dp) {
    tabContentWidths[tabKey] = width
  }

  fun indicatorWidth(tabKey: TabKey): Dp {
    val contentWidth = tabContentWidths[tabKey] ?: 0.dp
    return if (contentWidth > 24.dp) contentWidth else 24.dp
  }

  fun hasContentWidth(tabKey: TabKey): Boolean = tabContentWidths.containsKey(tabKey)

  fun setSelectionCallback(tabKey: TabKey, onClick: () -> Unit) {
    tabSelectionCallbacks[tabKey] = onClick
  }

  fun select(tabKey: TabKey) {
    tabSelectionCallbacks[tabKey]?.invoke()
  }
}

private class MaterialTabIndicatorState(
  initialOffset: Dp,
  initialWidth: Dp,
) {
  val offset = Animatable(initialOffset, Dp.VectorConverter)
  val width = Animatable(initialWidth, Dp.VectorConverter)
}

private val LocalMaterialTabRowContext = staticCompositionLocalOf<MaterialTabRowContext?> { null }
private val LocalMaterialTabModifier = staticCompositionLocalOf<Modifier> { Modifier }

private fun MaterialTabRowContext?.nextTabKey(): TabKey =
  this?.nextTabKey() ?: error("Tab must be placed inside PrimaryTabRow or SecondaryTabRow.")

@Composable
fun TabListScope<TabKey>.Tab(
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  text: @Composable (() -> Unit)? = null,
  icon: @Composable (() -> Unit)? = null,
  selectedContentColor: Color = LocalContentColor.current,
  unselectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
  interactionSource: MutableInteractionSource? = null,
) {
  val height = if (icon == null) PrimaryTabHeight else 64.dp
  val tabRowContext = LocalMaterialTabRowContext.current
  val tabKey = remember(tabRowContext) { tabRowContext.nextTabKey() }
  val density = LocalDensity.current
  val tabIndication = ripple(bounded = true, color = selectedContentColor)
  SideEffect {
    tabRowContext?.setSelectionCallback(tabKey, onClick)
  }

  CompositionLocalProvider(
    LocalContentColor provides if (selected) selectedContentColor else unselectedContentColor,
  ) {
    UnstyledTab(
      key = tabKey,
      enabled = enabled,
      activateOnFocus = false,
      modifier = modifier
        .then(LocalMaterialTabModifier.current)
        .height(height),
      contentPadding = PaddingValues(horizontal = TabHorizontalPadding),
      indication = tabIndication,
      interactionSource = interactionSource,
    ) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
          modifier = Modifier.onSizeChanged { size ->
            tabRowContext?.setContentWidth(
              tabKey = tabKey,
              width = with(density) { size.width.toDp() },
            )
          },
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
        ) {
          icon?.let {
            Box(Modifier.size(TabIconSize), contentAlignment = Alignment.Center) {
              it()
            }
            if (text != null) {
              Spacer(Modifier.height(TabIconTextPadding))
            }
          }
          ProvideTextStyle(MaterialTheme.typography.labelLarge) {
            text?.invoke()
          }
        }
      }
    }
  }
}

@Composable
fun TabListScope<TabKey>.Tab(
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  selectedContentColor: Color = LocalContentColor.current,
  unselectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
  interactionSource: MutableInteractionSource? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  val tabRowContext = LocalMaterialTabRowContext.current
  val tabKey = remember(tabRowContext) { tabRowContext.nextTabKey() }
  val density = LocalDensity.current
  val tabIndication = ripple(bounded = true, color = selectedContentColor)
  SideEffect {
    tabRowContext?.setSelectionCallback(tabKey, onClick)
  }

  CompositionLocalProvider(
    LocalContentColor provides if (selected) selectedContentColor else unselectedContentColor,
  ) {
    UnstyledTab(
      key = tabKey,
      enabled = enabled,
      activateOnFocus = false,
      modifier = modifier
        .then(LocalMaterialTabModifier.current)
        .fillMaxHeight(),
      contentPadding = PaddingValues(horizontal = TabHorizontalPadding),
      indication = tabIndication,
      interactionSource = interactionSource,
    ) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
          modifier = Modifier.onSizeChanged { size ->
            tabRowContext?.setContentWidth(
              tabKey = tabKey,
              width = with(density) { size.width.toDp() },
            )
          },
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          content = content,
        )
      }
    }
  }
}

@Composable
fun PrimaryTabRow(
  selectedTabIndex: Int,
  tabKeys: List<TabKey>,
  modifier: Modifier = Modifier,
  containerColor: Color = TabRowDefaults.primaryContainerColor,
  contentColor: Color = TabRowDefaults.primaryContentColor,
  indicator: @Composable TabIndicatorScope.() -> Unit = {},
  divider: @Composable () -> Unit = {},
  tabs: @Composable TabListScope<TabKey>.() -> Unit,
) {
  val selectedTab = tabKeys.getOrNull(selectedTabIndex)
    ?: error("selectedTabIndex must reference an entry in tabKeys.")
  val tabRowContext = remember(tabKeys) { MaterialTabRowContext(tabKeys) }
  var indicatorState by remember { mutableStateOf<MaterialTabIndicatorState?>(null) }
  val indicatorAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Dp>()
  val indicatorScope = rememberCoroutineScope()
  val selectedIndicatorWidth = tabRowContext.indicatorWidth(selectedTab)

  CompositionLocalProvider(LocalContentColor provides contentColor) {
    UnstyledTabGroup(
      selectedTab = selectedTab,
      onSelectedTabChange = { tabRowContext.select(it) },
      tabs = tabKeys,
      modifier = modifier.background(containerColor),
    ) {
      val tabGroup = this
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(PrimaryTabHeight),
      ) {
        val density = LocalDensity.current
        var tabRowSize by remember { mutableStateOf(IntSize.Zero) }
        val tabSlotWidth = with(density) {
          if (tabKeys.isNotEmpty()) (tabRowSize.width / tabKeys.size).toDp() else 0.dp
        }
        val targetIndicatorOffset = tabSlotWidth * selectedTabIndex +
          (tabSlotWidth - selectedIndicatorWidth) / 2
        val isIndicatorReady = tabRowSize != IntSize.Zero &&
          tabRowContext.hasContentWidth(selectedTab)
        LaunchedEffect(
          targetIndicatorOffset,
          selectedIndicatorWidth,
          tabRowSize,
          isIndicatorReady,
        ) {
          if (isIndicatorReady.not()) {
            return@LaunchedEffect
          }

          val currentIndicatorState = indicatorState
          if (currentIndicatorState == null) {
            indicatorState = MaterialTabIndicatorState(
              initialOffset = targetIndicatorOffset,
              initialWidth = selectedIndicatorWidth,
            )
          } else {
            indicatorScope.launch {
              currentIndicatorState.offset.animateTo(targetIndicatorOffset, indicatorAnimationSpec)
            }
            indicatorScope.launch {
              currentIndicatorState.width.animateTo(selectedIndicatorWidth, indicatorAnimationSpec)
            }
          }
        }

        tabRowContext.nextIndex = 0
        tabGroup.TabList(
          modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { tabRowSize = it },
        ) {
          val tabListScope = this
          Row(Modifier.fillMaxSize()) {
            CompositionLocalProvider(
              LocalMaterialTabRowContext provides tabRowContext,
              LocalMaterialTabModifier provides Modifier.weight(1f),
            ) {
              with(tabListScope) {
                tabs()
              }
            }
          }
        }

        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .height(TabDividerHeight)
            .background(MaterialTheme.colorScheme.outlineVariant),
        )
        divider()
        val currentIndicatorState = indicatorState
        if (isIndicatorReady) {
          val indicatorOffset = currentIndicatorState?.offset?.value ?: targetIndicatorOffset
          val indicatorWidth = currentIndicatorState?.width?.value ?: selectedIndicatorWidth
          Box(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .height(PrimaryTabIndicatorHeight)
              .layout { measurable, constraints ->
                val placeable = measurable.measure(
                  constraints.copy(
                    minWidth = indicatorWidth.roundToPx(),
                    maxWidth = indicatorWidth.roundToPx(),
                  ),
                )
                layout(placeable.width, placeable.height) {
                  placeable.place(indicatorOffset.roundToPx(), 0)
                }
              }
              .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
              .background(contentColor),
          )
        }
      }
    }
  }
}

@Composable
fun SecondaryTabRow(
  selectedTabIndex: Int,
  tabKeys: List<TabKey>,
  modifier: Modifier = Modifier,
  containerColor: Color = TabRowDefaults.secondaryContainerColor,
  contentColor: Color = TabRowDefaults.secondaryContentColor,
  indicator: @Composable TabIndicatorScope.() -> Unit = {},
  divider: @Composable () -> Unit = {},
  tabs: @Composable TabListScope<TabKey>.() -> Unit,
) {
  val selectedTab = tabKeys.getOrNull(selectedTabIndex)
    ?: error("selectedTabIndex must reference an entry in tabKeys.")
  val tabRowContext = remember(tabKeys) { MaterialTabRowContext(tabKeys) }
  var indicatorState by remember { mutableStateOf<MaterialTabIndicatorState?>(null) }
  val indicatorAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Dp>()
  val indicatorScope = rememberCoroutineScope()

  CompositionLocalProvider(LocalContentColor provides contentColor) {
    UnstyledTabGroup(
      selectedTab = selectedTab,
      onSelectedTabChange = { tabRowContext.select(it) },
      tabs = tabKeys,
      modifier = modifier.background(containerColor),
    ) {
      val tabGroup = this
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(PrimaryTabHeight),
      ) {
        val density = LocalDensity.current
        var tabRowSize by remember { mutableStateOf(IntSize.Zero) }
        val tabSlotWidth = with(density) {
          if (tabKeys.isNotEmpty()) (tabRowSize.width / tabKeys.size).toDp() else 0.dp
        }
        val targetIndicatorOffset = tabSlotWidth * selectedTabIndex
        val isIndicatorReady = tabRowSize != IntSize.Zero

        LaunchedEffect(targetIndicatorOffset, tabSlotWidth, tabRowSize, isIndicatorReady) {
          if (isIndicatorReady.not()) {
            return@LaunchedEffect
          }

          val currentIndicatorState = indicatorState
          if (currentIndicatorState == null) {
            indicatorState = MaterialTabIndicatorState(
              initialOffset = targetIndicatorOffset,
              initialWidth = tabSlotWidth,
            )
          } else {
            indicatorScope.launch {
              currentIndicatorState.offset.animateTo(targetIndicatorOffset, indicatorAnimationSpec)
            }
            indicatorScope.launch {
              currentIndicatorState.width.animateTo(tabSlotWidth, indicatorAnimationSpec)
            }
          }
        }

        tabRowContext.nextIndex = 0
        tabGroup.TabList(
          modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { tabRowSize = it },
        ) {
          val tabListScope = this
          Row(Modifier.fillMaxSize()) {
            CompositionLocalProvider(
              LocalMaterialTabRowContext provides tabRowContext,
              LocalMaterialTabModifier provides Modifier.weight(1f),
            ) {
              with(tabListScope) {
                tabs()
              }
            }
          }
        }

        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .height(TabDividerHeight)
            .background(MaterialTheme.colorScheme.outlineVariant),
        )
        divider()
        val currentIndicatorState = indicatorState
        if (isIndicatorReady) {
          val indicatorOffset = currentIndicatorState?.offset?.value ?: targetIndicatorOffset
          val indicatorWidth = currentIndicatorState?.width?.value ?: tabSlotWidth
          Box(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .height(SecondaryTabIndicatorHeight)
              .layout { measurable, constraints ->
                val placeable = measurable.measure(
                  constraints.copy(
                    minWidth = indicatorWidth.roundToPx(),
                    maxWidth = indicatorWidth.roundToPx(),
                  ),
                )
                layout(placeable.width, placeable.height) {
                  placeable.place(indicatorOffset.roundToPx(), 0)
                }
              }
              .background(MaterialTheme.colorScheme.primary),
          )
        }
      }
    }
  }
}
