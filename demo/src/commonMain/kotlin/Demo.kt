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
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.composeunstyled.CrossAxisAlignment
import com.composeunstyled.MainAxisArrangement
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.currentWindowContainerSize
import com.composeunstyled.theme.buildTheme

private val DemoTheme = buildTheme {
  name = "DemoTheme"
  defaultTextStyle = TextStyle(fontFamily = FontFamily.Monospace)
  defaultContentColor = Color.Black
}

@Composable
fun Demo(startDestination: String = "home") {
  DemoTheme {
    CompositionLocalProvider(LocalIndication provides PrototypeIndication) {
      Box(Modifier.fillMaxSize().background(Color.White).border(1.dp, Color.Black)) {
        DemoSelection(startDestination)
      }
    }
  }
}

private data class DemoItem(
  val name: String,
  val id: String,
  val demo: @Composable () -> Unit,
  val previewOptions: PreviewOptions = PreviewOptions(),
)

private data class PreviewOptions(
  val contentAlignment: Alignment = Alignment.Center,
  val padding: PaddingValues = PaddingValues(16.dp),
)

private val availablePrimitives = listOf(
  DemoItem("AVATAR", "avatar", { AvatarDemo() }),
  DemoItem(
    "BOTTOM SHEET",
    "bottom-sheet",
    { BottomSheetDemo() },
    previewOptions = PreviewOptions(padding = PaddingValues(0.dp)),
  ),
  DemoItem("MODAL BOTTOM SHEET", "modal-bottom-sheet", { ModalBottomSheetDemo() }),
  DemoItem("BUTTON", "button", { ButtonDemo() }),
  DemoItem("CHECKBOX", "checkbox", { CheckboxDemo() }),
  DemoItem("TRISTATE CHECKBOX", "tristatecheckbox", { TriStateCheckboxDemo() }),
  DemoItem("DIALOG", "dialog", { DialogDemo() }),
  DemoItem(
    "DISCLOSURE",
    "disclosure",
    { DisclosureDemo() },
    previewOptions = PreviewOptions(contentAlignment = Alignment.TopCenter),
  ),
  DemoItem(
    "DROPDOWN MENU",
    "dropdown-menu",
    { DropdownMenuDemo() },
    previewOptions = PreviewOptions(contentAlignment = Alignment.TopCenter),
  ),
  DemoItem("ICON", "icon", { IconDemo() }),
  DemoItem("MODAL", "modal", { ModalDemo() }),
  DemoItem("MODAL RENDER HOST", "modal-render-host", { ModalRenderHostDemo() }),
  DemoItem("PROGRESS INDICATOR", "progressindicator", { ProgressIndicatorDemo() }),
  DemoItem("RADIO GROUP", "radiogroup", { RadioGroupDemo() }),
  DemoItem("SCROLLBARS", "scrollbars", { ScrollbarsDemo() }),
  DemoItem("SEPARATORS", "separators", { SeparatorsDemo() }),
  DemoItem("SLIDER", "slider", { SliderDemo() }),
  DemoItem("TAB GROUP", "tabgroup", { TabGroupDemo() }),
  DemoItem("TEXT FIELD", "textfield", { TextFieldDemo() }),
  DemoItem("TOOLTIP", "tooltip", { TooltipDemo() }),
  DemoItem("TOGGLE SWITCH", "toggleswitch", { ToggleSwitchDemo() }),
)

private val availableModifiers = listOf(
  DemoItem(
    "FOCUS RING (FOCUSVISIBLE)",
    "focus-ring-focus-visible",
    { FocusRingFocusVisibleDemo() },
  ),
  DemoItem("FOCUS RING (FOCUSED)", "focus-ring-focused", { FocusRingFocusedDemo() }),
  DemoItem("OUTLINE", "outline", { OutlineDemo() }),
).map {
  it.copy(demo = {
    ModifierDemo {
      it.demo()
    }
  })
}

private val themingDemos = listOf(
  DemoItem("PLATFORM THEME", "platform-theme", { PlatformThemeDemo() }),
  DemoItem("THEME EXTEND", "theme-extend", { ThemeExtendDemo() }),
  DemoItem("THEMING", "theme", { ThemingDemo() }),
)

private val utilityDemos = listOf(
  DemoItem("BREAKPOINTS", "breakpoints", { BreakpointsDemo() }),
  DemoItem("WINDOW CONTAINER SIZE", "window-container-size", { WindowContainerSizeDemo() }),
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
private fun DemoSelection(startDestination: String) {
  val navController = rememberNavController()
  val initialDestination = availableDemos
    .firstOrNull { it.id == startDestination }
    ?.id
    ?: "home"

  NavHost(
    navController = navController,
    startDestination = initialDestination,
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
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart,
      ) {
        Column(
          Modifier
            .verticalScroll(rememberScrollState())
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(8.dp)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          if (availablePrimitives.isNotEmpty()) {
            DemoSection("COMPONENTS", availablePrimitives) { demo ->
              navController.navigate(demo.id)
            }
          }

          if (themingDemos.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            DemoSection("THEME", themingDemos) { demo ->
              navController.navigate(demo.id)
            }
          }

          if (availableModifiers.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            DemoSection("MODIFIERS", availableModifiers) { demo ->
              navController.navigate(demo.id)
            }
          }

          if (utilityDemos.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            DemoSection("UTILITIES", utilityDemos) { demo ->
              navController.navigate(demo.id)
            }
          }
        }
      }
    }

    availableDemos.forEach { component ->
      composable(component.id) {
        val launchedFromDemoList = initialDestination == "home"
        Column(Modifier.fillMaxSize()) {
          if (launchedFromDemoList) {
            AppBar(onUpClick = { navController.navigateUp() }, title = component.name)
          }
          Box(Modifier.weight(1f)) {
            DemoContainer(component.previewOptions) {
              component.demo()
            }
          }
        }
      }
    }
  }
}

@Composable
private fun DemoContainer(
  previewOptions: PreviewOptions,
  content: @Composable () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
      .padding(previewOptions.padding),
    contentAlignment = previewOptions.contentAlignment,
  ) {
    content()
  }
}

@Composable
private fun DemoSection(
  title: String,
  demos: List<DemoItem>,
  onClick: (DemoItem) -> Unit,
) {
  Text(
    text = title,
    modifier = Modifier.padding(horizontal = 16.dp),
    color = Color.Black,
    fontWeight = FontWeight.SemiBold,
  )
  demos.forEach { demo ->
    DemoListButton(
      onClick = { onClick(demo) },
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(demo.name, color = Color.Black)
    }
  }
}

@Composable
private fun AppBar(onUpClick: () -> Unit, title: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color.White)
      .padding(WindowInsets.statusBars.asPaddingValues())
      .padding(4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    UnstyledButton(
      onClick = onUpClick,
      modifier = Modifier
        .clip(RectangleShape).border(1.dp, Color.Black),
      indication = LocalIndication.current,
    ) {
      Box(Modifier.padding(12.dp)) {
        Text("BACK")
      }
    }
    Spacer(Modifier.width(8.dp))
    Text(title, color = Color.Black)
  }
}

@Composable
private fun DemoListButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  UnstyledButton(
    onClick = onClick,
    modifier = modifier
      .sizeIn(minWidth = 40.dp, minHeight = 48.dp)
      .clip(RectangleShape).border(1.dp, Color.Black),
    indication = LocalIndication.current,
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      contentAlignment = Alignment.CenterStart,
    ) {
      content()
    }
  }
}
