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
package com.composeunstyled.demo

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.X
import com.composeunstyled.CrossAxisAlignment
import com.composeunstyled.MainAxisArrangement
import com.composeunstyled.PortalHost
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.currentWindowContainerSize
import com.composeunstyled.focusRing
import com.composeunstyled.outline

@Composable
fun Demo(demoId: String? = null) {
  MaterialTheme {
    PortalHost(Modifier.fillMaxSize()) {
      Box(Modifier.fillMaxSize().background(Color(0xFFFAFAFA))) {
        if (demoId == null) {
          DemoSelection()
        } else {
          (
            availableDemos.firstOrNull { it.id == demoId }
              ?: error("Demo not found: $demoId")
            )
            .demo()
        }
      }
    }
  }
}

private data class DemoItem(val name: String, val id: String, val demo: @Composable () -> Unit)

private val availablePrimitives = listOf(
  DemoItem("Bottom Sheet", "bottom-sheet", { BottomSheetDemo() }),
  DemoItem("Bottom Sheet (Modal)", "modal-bottom-sheet", { ModalBottomSheetDemo() }),
  DemoItem("Button", "button", { ButtonDemo() }),
  DemoItem("Checkbox", "checkbox", { CheckboxDemo() }),
  DemoItem("Checkbox (TriState)", "tristatecheckbox", { TriStateCheckboxDemo() }),
  DemoItem("Dialog", "dialog", { DialogDemo() }),
  DemoItem("Disclosure", "disclosure", { DisclosureDemo() }),
  DemoItem("Dropdown Menu", "dropdown-menu", { DropdownMenuDemo() }),
  DemoItem("Icon", "icon", { IconDemo() }),
  DemoItem("Modal", "modal", { ModalDemo() }),
  DemoItem("Progress Indicator", "progressindicator", { ProgressIndicatorDemo() }),
  DemoItem("Radio Group", "radiogroup", { RadioGroupDemo() }),
  DemoItem("Scrollbars", "scrollbars", { ScrollbarsDemo() }),
  DemoItem("Separators", "separators", { SeparatorsDemo() }),
  DemoItem("Slider", "slider", { SliderDemo() }),
  DemoItem("Tab Group", "tabgroup", { TabGroupDemo() }),
  DemoItem("Text Input", "textinput", { TextInputDemo() }),
  DemoItem("Tooltip", "tooltip", { TooltipDemo() }),
  DemoItem("Toggle Switch", "toggleswitch", { ToggleSwitchDemo() }),
)

private val availableModifiers = listOf(
  DemoItem("Focus Ring", "focus-ring", { FocusRingDemo() }),
  DemoItem("Outline", "outline", { OutlineDemo() }),
).map { it ->
  it.copy(demo = {
    ModifierDemo {
      it.demo()
    }
  })
}

private val themingDemos = listOf(
  DemoItem("Platform Theme", "platform-theme") { PlatformThemeDemo() },
  DemoItem("Theming", "theme") { ThemingDemo() },
)

private val utilityDemos = listOf(
  DemoItem("Window Container Size", "window-container-size", { WindowContainerSizeDemo() }),
)

private val availableDemos: List<DemoItem> =
  availablePrimitives + availableModifiers + themingDemos + utilityDemos

@Composable
fun ModifierDemo(content: @Composable () -> Unit) {
  val size = currentWindowContainerSize()
  val isWide = size.width > 600.dp
  val spacedBy = if (isWide) 60.dp else 30.dp
  Stack(
    modifier = Modifier.fillMaxSize().background(Color.White),
    orientation = if (isWide) StackOrientation.Horizontal else StackOrientation.Vertical,
    mainAxisArrangement = MainAxisArrangement.Center,
    crossAxisAlignment = CrossAxisAlignment.Center,
    spacing = spacedBy,
  ) {
    content()
  }
}

@Composable
private fun DemoSelection() {
  val navController = rememberNavController()
  var filterQuery by remember { mutableStateOf("") }
  var filterVisible by remember { mutableStateOf(false) }

  NavHost(
    navController = navController,
    startDestination = "home",
    enterTransition = {
      EnterTransition.None
    },
    exitTransition = {
      ExitTransition.None
    },
    popEnterTransition = {
      EnterTransition.None
    },
    popExitTransition = {
      ExitTransition.None
    },
  ) {
    composable("home") {
      val filteredThemingDemos = remember(filterQuery) {
        themingDemos.filterBy(filterQuery)
      }
      val filteredPrimitives = remember(filterQuery) {
        availablePrimitives.filterBy(filterQuery)
      }
      val filteredModifiers = remember(filterQuery) {
        availableModifiers.filterBy(filterQuery)
      }
      val filteredUtilities = remember(filterQuery) {
        utilityDemos.filterBy(filterQuery)
      }
      val homeFocusRequester = remember { FocusRequester() }

      LaunchedEffect(Unit) {
        homeFocusRequester.requestFocus()
      }

      Box(
        modifier = Modifier
          .fillMaxSize()
          .focusRequester(homeFocusRequester)
          .focusable()
          .onPreviewKeyEvent { event ->
            when {
              event.type == KeyEventType.KeyDown && event.key == Key.F && event.isMetaPressed -> {
                filterVisible = true
                true
              }

              event.type == KeyEventType.KeyDown && event.key == Key.Escape && filterVisible -> {
                filterQuery = ""
                filterVisible = false
                true
              }

              else -> false
            }
          },
        contentAlignment = Alignment.Center,
      ) {
        Column(
          Modifier.verticalScroll(rememberScrollState()).systemBarsPadding().padding(16.dp)
            .widthIn(max = 600.dp).fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          if (filteredPrimitives.isNotEmpty()) {
            DemoSection("Components", filteredPrimitives) { demo ->
              navController.navigate(demo.id)
            }
          }

          if (filteredThemingDemos.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            DemoSection("Theme", filteredThemingDemos) { demo ->
              navController.navigate(demo.id)
            }
          }

          if (filteredModifiers.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            DemoSection("Modifiers", filteredModifiers) { demo ->
              navController.navigate(demo.id)
            }
          }

          if (filteredUtilities.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            DemoSection("Utilities", filteredUtilities) { demo ->
              navController.navigate(demo.id)
            }
          }

          if (
            filteredThemingDemos.isEmpty() &&
            filteredPrimitives.isEmpty() &&
            filteredModifiers.isEmpty() &&
            filteredUtilities.isEmpty()
          ) {
            Text(
              "No demos match \"$filterQuery\"",
              modifier = Modifier.padding(24.dp).align(Alignment.CenterHorizontally),
              color = Color.Black.copy(alpha = 0.58f),
              style = MaterialTheme.typography.bodyMedium,
            )
          }
        }

        if (filterVisible || filterQuery.isNotEmpty()) {
          DemoFilterBox(
            value = filterQuery,
            onValueChange = { filterQuery = it },
            onDismiss = {
              filterQuery = ""
              filterVisible = false
            },
            modifier = Modifier.align(Alignment.TopEnd).systemBarsPadding().padding(16.dp),
          )
        }
      }
    }

    availableDemos.forEach { component ->
      composable(component.id) {
        Column {
          AppBar(onUpClick = { navController.navigateUp() }, title = component.name)
          Box(
            Modifier.fillMaxWidth().weight(1f),
          ) {
            component.demo()
          }
        }
      }
    }
  }
}

private fun List<DemoItem>.filterBy(query: String): List<DemoItem> {
  val normalizedQuery = query.trim()
  if (normalizedQuery.isEmpty()) return this
  return filter { demo ->
    demo.name.contains(normalizedQuery, ignoreCase = true) ||
      demo.id.contains(normalizedQuery, ignoreCase = true)
  }
}

@Composable
private fun DemoSection(
  title: String,
  demos: List<DemoItem>,
  onClick: (DemoItem) -> Unit,
) {
  Title(title)
  demos.forEach { demo ->
    DemoListButton(
      onClick = { onClick(demo) },
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(demo.name)
    }
  }
}

@Composable
private fun DemoFilterBox(
  value: String,
  onValueChange: (String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val focusRequester = remember { FocusRequester() }
  val shape = RoundedCornerShape(100)

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  Row(
    modifier = modifier
      .widthIn(min = 260.dp, max = 360.dp)
      .shadow(12.dp, shape)
      .background(Color.White, shape)
      .outline(1.dp, Color.Black.copy(alpha = 0.08f), shape)
      .padding(horizontal = 14.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    UnstyledIcon(Lucide.Search, contentDescription = null, tint = Color.Black)
    Spacer(Modifier.width(10.dp))
    BasicTextField(
      value = value,
      onValueChange = onValueChange,
      modifier = Modifier.weight(1f).focusRequester(focusRequester),
      singleLine = true,
      textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
      decorationBox = { innerTextField ->
        Box {
          if (value.isEmpty()) {
            Text(
              "Filter demos...",
              color = Color.Black.copy(alpha = 0.42f),
              style = MaterialTheme.typography.bodyLarge,
            )
          }
          innerTextField()
        }
      },
    )
    Spacer(Modifier.width(10.dp))
    val interactionSource = remember { MutableInteractionSource() }
    UnstyledButton(
      onClick = onDismiss,
      interactionSource = interactionSource,
      contentPadding = PaddingValues(6.dp),
      modifier = Modifier
        .clip(CircleShape)
        .focusRing(interactionSource, 1.dp, Color.Blue, CircleShape),
    ) {
      UnstyledIcon(Lucide.X, contentDescription = "Close filter", tint = Color.Black)
    }
  }
}

@Composable
private fun Title(text: String) {
  Text(
    text,
    modifier = Modifier.padding(bottom = 8.dp),
    style = MaterialTheme.typography.titleMedium,
  )
}

@Composable
private fun AppBar(onUpClick: () -> Unit, title: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .shadow(12.dp)
      .background(Color.White)
      .padding(WindowInsets.statusBars.asPaddingValues())
      .padding(4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    val interactionSource = remember { MutableInteractionSource() }
    UnstyledButton(
      onClick = onUpClick,
      interactionSource = interactionSource,
      contentPadding = PaddingValues(12.dp),
      modifier = Modifier
        .clip(CircleShape)
        .focusRing(interactionSource, 1.dp, Color.Blue, CircleShape),
    ) {
      UnstyledIcon(Lucide.ArrowLeft, contentDescription = "Go back")
    }
    Spacer(Modifier.width(8.dp))
    Text(title, style = MaterialTheme.typography.titleMedium)
  }
}

@Composable
private fun DemoListButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  UnstyledButton(
    onClick = onClick,
    interactionSource = interactionSource,
    modifier = modifier
      .focusRing(interactionSource, 1.dp, Color.Blue, RoundedCornerShape(8.dp))
      .shadow(2.dp, RoundedCornerShape(8.dp))
      .sizeIn(minWidth = 40.dp, minHeight = 40.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(Color.White)
      .outline(1.dp, Color.Black.copy(0.1f), RoundedCornerShape(8.dp)),
    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
  ) {
    content()
  }
}
